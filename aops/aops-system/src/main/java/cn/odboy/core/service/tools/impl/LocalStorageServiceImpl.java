package cn.odboy.core.service.tools.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.odboy.core.dal.dataobject.tools.LocalStorage;
import cn.odboy.core.dal.mysql.tools.LocalStorageMapper;
import cn.odboy.core.framework.properties.AppProperties;
import cn.odboy.core.service.tools.LocalStorageService;
import cn.odboy.exception.BadRequestException;
import cn.odboy.util.FileUtil;
import cn.odboy.util.StringUtil;
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
public class LocalStorageServiceImpl extends ServiceImpl<LocalStorageMapper, LocalStorage> implements LocalStorageService {
    private final LocalStorageMapper localStorageMapper;
    private final AppProperties properties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LocalStorage uploadFile(String name, MultipartFile multipartFile) {
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
            LocalStorage localStorage = new LocalStorage(
                    file.getName(),
                    name,
                    suffix,
                    file.getPath(),
                    type,
                    formatSize
            );
            save(localStorage);
            return localStorage;
        } catch (Exception e) {
            FileUtil.del(file);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyLocalStorageById(LocalStorage resources) {
        LocalStorage localStorage = getById(resources.getId());
        localStorage.copy(resources);
        saveOrUpdate(localStorage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFileByIds(Long[] ids) {
        for (Long id : ids) {
            LocalStorage storage = getById(id);
            FileUtil.del(storage.getPath());
            removeById(storage);
        }
    }

    @Override
    public void downloadExcel(List<LocalStorage> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LocalStorage localStorage : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("文件名", localStorage.getRealName());
            map.put("备注名", localStorage.getName());
            map.put("文件类型", localStorage.getType());
            map.put("文件大小", localStorage.getSize());
            map.put("创建者", localStorage.getCreateBy());
            map.put("创建日期", localStorage.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
