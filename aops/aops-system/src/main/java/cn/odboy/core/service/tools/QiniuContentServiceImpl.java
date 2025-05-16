package cn.odboy.core.service.tools;

import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.PageUtil;
import cn.odboy.core.dal.dataobject.tools.QiniuConfigDO;
import cn.odboy.core.dal.dataobject.tools.QiniuContentDO;
import cn.odboy.core.dal.mysql.tools.QiniuContentMapper;
import cn.odboy.core.service.tools.dto.QiniuQueryArgs;
import cn.odboy.core.service.tools.util.QiniuUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class QiniuContentServiceImpl extends ServiceImpl<QiniuContentMapper, QiniuContentDO> implements QiniuContentService {
    private final QiniuContentMapper qiniuContentMapper;
    private final QiniuConfigService qiniuConfigService;

    @Value("${app.oss.qiniu.max-size}")
    private Long maxSize;

    @Override
    public PageResult<QiniuContentDO> describeQiniuContentPage(QiniuQueryArgs args, Page<Object> page) {
        return PageUtil.toPage(qiniuContentMapper.queryQiniuContentPageByArgs(args, page));
    }

    @Override
    public List<QiniuContentDO> describeQiniuContentList(QiniuQueryArgs args) {
        return qiniuContentMapper.queryQiniuContentPageByArgs(args, PageUtil.getCount(qiniuContentMapper)).getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QiniuContentDO uploadFile(MultipartFile file) {
        FileUtil.checkSize(maxSize, file.getSize());
        QiniuConfigDO qiniuConfigDO = qiniuConfigService.describeQiniuConfig();
        if (qiniuConfigDO.getId() == null) {
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiniuUtil.getRegion(qiniuConfigDO.getZone()));
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(qiniuConfigDO.getAccessKey(), qiniuConfigDO.getSecretKey());
        String upToken = auth.uploadToken(qiniuConfigDO.getBucket());
        try {
            String key = file.getOriginalFilename();
            if (qiniuContentMapper.getQiniuContentByName(key) != null) {
                key = QiniuUtil.getKey(key);
            }
            Response response = uploadManager.put(file.getBytes(), key, upToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            String fileNameNoExt = FileUtil.getPrefix(putRet.key);
            QiniuContentDO content = qiniuContentMapper.getQiniuContentByName(fileNameNoExt);
            if (content == null) {
                // 存入数据库
                QiniuContentDO qiniuContentDO = new QiniuContentDO();
                qiniuContentDO.setSuffix(FileUtil.getSuffix(putRet.key));
                qiniuContentDO.setBucket(qiniuConfigDO.getBucket());
                qiniuContentDO.setType(qiniuConfigDO.getType());
                qiniuContentDO.setKey(fileNameNoExt);
                qiniuContentDO.setUrl(qiniuConfigDO.getHost() + "/" + putRet.key);
                qiniuContentDO.setSize(FileUtil.getSize(Integer.parseInt(String.valueOf(file.getSize()))));
                save(qiniuContentDO);
            }
            return content;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public String createFilePreviewUrl(QiniuContentDO content) {
        QiniuConfigDO qiniuConfigDO = qiniuConfigService.describeQiniuConfig();
        String finalUrl;
        String type = "公开";
        if (type.equals(content.getType())) {
            finalUrl = content.getUrl();
        } else {
            Auth auth = Auth.create(qiniuConfigDO.getAccessKey(), qiniuConfigDO.getSecretKey());
            // 1小时，可以自定义链接过期时间
            long expireInSeconds = 3600;
            finalUrl = auth.privateDownloadUrl(content.getUrl(), expireInSeconds);
        }
        return finalUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFileById(Long id) {
        QiniuConfigDO qiniuConfigDO = qiniuConfigService.describeQiniuConfig();
        QiniuContentDO qiniuContentDO = qiniuContentMapper.selectById(id);
        if (qiniuContentDO == null) {
            throw new BadRequestException("文件不存在");
        }
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiniuUtil.getRegion(qiniuConfigDO.getZone()));
        Auth auth = Auth.create(qiniuConfigDO.getAccessKey(), qiniuConfigDO.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(qiniuContentDO.getBucket(), qiniuContentDO.getKey() + "." + qiniuContentDO.getSuffix());
        } catch (QiniuException ex) {
            log.error("七牛云删除文件失败", ex);
        } finally {
            removeById(qiniuContentDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronize() {
        QiniuConfigDO qiniuConfigDO = qiniuConfigService.describeQiniuConfig();
        if (qiniuConfigDO.getId() == null) {
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiniuUtil.getRegion(qiniuConfigDO.getZone()));
        Auth auth = Auth.create(qiniuConfigDO.getAccessKey(), qiniuConfigDO.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";
        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(qiniuConfigDO.getBucket(), prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            QiniuContentDO qiniuContentDO;
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                String filename = FileUtil.getPrefix(item.key);
                String suffix = FileUtil.getSuffix(item.key);
                if (qiniuContentMapper.getQiniuContentByName(filename) == null) {
                    qiniuContentDO = new QiniuContentDO();
                    qiniuContentDO.setSize(FileUtil.getSize(Integer.parseInt(String.valueOf(item.fsize))));
                    qiniuContentDO.setSuffix(suffix);
                    qiniuContentDO.setKey(filename);
                    qiniuContentDO.setType(qiniuConfigDO.getType());
                    qiniuContentDO.setBucket(qiniuConfigDO.getBucket());
                    qiniuContentDO.setUrl(qiniuConfigDO.getHost() + "/" + item.key);
                    save(qiniuContentDO);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFileByIds(Long[] ids) {
        for (Long id : ids) {
            QiniuContentDO qiniuContentDO = getById(id);
            if (qiniuContentDO != null) {
                removeFileById(id);
            }
        }
    }

    @Override
    public void downloadExcel(List<QiniuContentDO> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QiniuContentDO content : queryAll) {
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
