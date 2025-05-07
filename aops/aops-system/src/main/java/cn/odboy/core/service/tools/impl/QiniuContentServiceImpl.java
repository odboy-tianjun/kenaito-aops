package cn.odboy.core.service.tools.impl;

import cn.odboy.core.api.tools.QiniuConfigApi;
import cn.odboy.core.dal.dataobject.tools.QiniuConfig;
import cn.odboy.core.dal.dataobject.tools.QiniuContent;
import cn.odboy.core.dal.mysql.tools.QiniuContentMapper;
import cn.odboy.core.service.tools.QiniuContentService;
import cn.odboy.core.util.QiniuUtil;
import cn.odboy.exception.BadRequestException;
import cn.odboy.util.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "qiNiu")
public class QiniuContentServiceImpl extends ServiceImpl<QiniuContentMapper, QiniuContent> implements QiniuContentService {
    private final QiniuContentMapper qiniuContentMapper;
    private final QiniuConfigApi qiniuConfigApi;

    @Value("${app.oss.qiniu.max-size}")
    private Long maxSize;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QiniuContent uploadFile(MultipartFile file) {
        FileUtil.checkSize(maxSize, file.getSize());
        QiniuConfig qiniuConfig = qiniuConfigApi.describeQiniuConfig();
        if (qiniuConfig.getId() == null) {
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiniuUtil.getRegion(qiniuConfig.getZone()));
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        String upToken = auth.uploadToken(qiniuConfig.getBucket());
        try {
            String key = file.getOriginalFilename();
            if (qiniuContentMapper.getQiniuContentByName(key) != null) {
                key = QiniuUtil.getKey(key);
            }
            Response response = uploadManager.put(file.getBytes(), key, upToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            String fileNameNoExt = FileUtil.getPrefix(putRet.key);
            QiniuContent content = qiniuContentMapper.getQiniuContentByName(fileNameNoExt);
            if (content == null) {
                // 存入数据库
                QiniuContent qiniuContent = new QiniuContent();
                qiniuContent.setSuffix(FileUtil.getSuffix(putRet.key));
                qiniuContent.setBucket(qiniuConfig.getBucket());
                qiniuContent.setType(qiniuConfig.getType());
                qiniuContent.setKey(fileNameNoExt);
                qiniuContent.setUrl(qiniuConfig.getHost() + "/" + putRet.key);
                qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(String.valueOf(file.getSize()))));
                save(qiniuContent);
            }
            return content;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public String createFilePreviewUrl(QiniuContent content) {
        QiniuConfig qiniuConfig = qiniuConfigApi.describeQiniuConfig();
        String finalUrl;
        String type = "公开";
        if (type.equals(content.getType())) {
            finalUrl = content.getUrl();
        } else {
            Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
            // 1小时，可以自定义链接过期时间
            long expireInSeconds = 3600;
            finalUrl = auth.privateDownloadUrl(content.getUrl(), expireInSeconds);
        }
        return finalUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFileById(Long id) {
        QiniuConfig qiniuConfig = qiniuConfigApi.describeQiniuConfig();
        QiniuContent qiniuContent = qiniuContentMapper.selectById(id);
        if (qiniuContent == null) {
            throw new BadRequestException("文件不存在");
        }
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiniuUtil.getRegion(qiniuConfig.getZone()));
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(qiniuContent.getBucket(), qiniuContent.getKey() + "." + qiniuContent.getSuffix());
        } catch (QiniuException ex) {
            log.error("七牛云删除文件失败", ex);
        } finally {
            removeById(qiniuContent);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronize() {
        QiniuConfig qiniuConfig = qiniuConfigApi.describeQiniuConfig();
        if (qiniuConfig.getId() == null) {
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiniuUtil.getRegion(qiniuConfig.getZone()));
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";
        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(qiniuConfig.getBucket(), prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            QiniuContent qiniuContent;
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                String filename = FileUtil.getPrefix(item.key);
                String suffix = FileUtil.getSuffix(item.key);
                if (qiniuContentMapper.getQiniuContentByName(filename) == null) {
                    qiniuContent = new QiniuContent();
                    qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(String.valueOf(item.fsize))));
                    qiniuContent.setSuffix(suffix);
                    qiniuContent.setKey(filename);
                    qiniuContent.setType(qiniuConfig.getType());
                    qiniuContent.setBucket(qiniuConfig.getBucket());
                    qiniuContent.setUrl(qiniuConfig.getHost() + "/" + item.key);
                    save(qiniuContent);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFileByIds(Long[] ids) {
        for (Long id : ids) {
            QiniuContent qiniuContent = getById(id);
            if (qiniuContent != null) {
                removeFileById(id);
            }
        }
    }

    @Override
    public void downloadExcel(List<QiniuContent> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QiniuContent content : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("文件名", content.getKey());
            map.put("文件类型", content.getSuffix());
            map.put("空间名称", content.getBucket());
            map.put("文件大小", content.getSize());
            map.put("空间类型", content.getType());
            map.put("创建日期", content.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
