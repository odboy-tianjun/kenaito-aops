package cn.odboy.core.service.tools;

import cn.odboy.common.model.PageResult;
import cn.odboy.core.dal.dataobject.tools.LocalStorageDO;
import cn.odboy.core.service.tools.dto.LocalStorageQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface LocalStorageService extends IService<LocalStorageDO> {
    /**
     * 分页查询
     *
     * @param args 条件
     * @param page 分页参数
     * @return /
     */
    PageResult<LocalStorageDO> describeLocalStoragePage(LocalStorageQueryArgs args, Page<Object> page);

    /**
     * 查询全部数据
     *
     * @param args 条件
     * @return /
     */
    List<LocalStorageDO> describeLocalStorageList(LocalStorageQueryArgs args);

    /**
     * 上传
     *
     * @param name 文件名称
     * @param file 文件
     * @return /
     */
    LocalStorageDO uploadFile(String name, MultipartFile file);

    /**
     * 编辑
     *
     * @param resources 文件信息
     */
    void modifyLocalStorageById(LocalStorageDO resources);

    /**
     * 多选删除
     *
     * @param ids /
     */
    void removeFileByIds(Long[] ids);

    /**
     * 导出数据
     *
     * @param localStorageDOS 待导出的数据
     * @param response        /
     * @throws IOException /
     */
    void downloadExcel(List<LocalStorageDO> localStorageDOS, HttpServletResponse response) throws IOException;
}
