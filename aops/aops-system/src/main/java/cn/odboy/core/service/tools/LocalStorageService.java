package cn.odboy.core.service.tools;

import cn.odboy.core.dal.dataobject.tools.LocalStorage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface LocalStorageService extends IService<LocalStorage> {
    /**
     * 上传
     *
     * @param name 文件名称
     * @param file 文件
     * @return /
     */
    LocalStorage uploadFile(String name, MultipartFile file);

    /**
     * 编辑
     *
     * @param resources 文件信息
     */
    void modifyLocalStorageById(LocalStorage resources);

    /**
     * 多选删除
     *
     * @param ids /
     */
    void removeFileByIds(Long[] ids);

    /**
     * 导出数据
     *
     * @param localStorages 待导出的数据
     * @param response      /
     * @throws IOException /
     */
    void downloadExcel(List<LocalStorage> localStorages, HttpServletResponse response) throws IOException;
}