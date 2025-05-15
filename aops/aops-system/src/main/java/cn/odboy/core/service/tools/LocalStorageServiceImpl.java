package cn.odboy.core.service.tools;

import cn.hutool.core.util.ObjectUtil;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.PageUtil;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.dal.dataobject.tools.LocalStorageDO;
import cn.odboy.core.dal.mysql.tools.LocalStorageMapper;
import cn.odboy.core.framework.system.config.AppProperties;
import cn.odboy.core.service.tools.dto.QueryLocalStorageArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class LocalStorageServiceImpl extends ServiceImpl<LocalStorageMapper, LocalStorageDO> implements LocalStorageService {
    private final LocalStorageMapper localStorageMapper;
    private final AppProperties properties;

    @Override
    public PageResult<LocalStorageDO> describeLocalStoragePage(QueryLocalStorageArgs args, Page<Object> page) {
        return PageUtil.toPage(localStorageMapper.queryLocalStoragePageByArgs(args, page));
    }

    @Override
    public List<LocalStorageDO> describeLocalStorageList(QueryLocalStorageArgs args) {
        return localStorageMapper.queryLocalStoragePageByArgs(args, PageUtil.getCount(localStorageMapper)).getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LocalStorageDO uploadFile(String name, MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        FileUtil.checkSize(properties.getFile().getFileMaxSize(), size);
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);
        File file = FileUtil.upload(multipartFile, properties.getFile().getPath() + type + File.separator);
        if (ObjectUtil.isNull(file)) {
            throw new BadRequestException("上传失败");
        }
        try {
            String formatSize = FileUtil.getSize(size);
            name = StringUtil.isBlank(name) ? FileUtil.getPrefix(multipartFile.getOriginalFilename()) : name;
            LocalStorageDO localStorageDO = new LocalStorageDO(
                    file.getName(),
                    name,
                    suffix,
                    file.getPath(),
                    type,
                    formatSize
            );
            save(localStorageDO);
            return localStorageDO;
        } catch (Exception e) {
            FileUtil.del(file);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyLocalStorageById(LocalStorageDO resources) {
        LocalStorageDO localStorageDO = getById(resources.getId());
        localStorageDO.copy(resources);
        saveOrUpdate(localStorageDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFileByIds(Long[] ids) {
        for (Long id : ids) {
            LocalStorageDO storage = getById(id);
            FileUtil.del(storage.getPath());
            removeById(storage);
        }
    }

    @Override
    public void downloadExcel(List<LocalStorageDO> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LocalStorageDO localStorageDO : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("文件名", localStorageDO.getRealName());
            map.put("备注名", localStorageDO.getName());
            map.put("文件类型", localStorageDO.getType());
            map.put("文件大小", localStorageDO.getSize());
            map.put("创建者", localStorageDO.getCreateBy());
            map.put("创建日期", localStorageDO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
