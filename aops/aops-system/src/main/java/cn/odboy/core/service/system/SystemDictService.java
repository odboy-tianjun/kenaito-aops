package cn.odboy.core.service.system;

import cn.odboy.core.dal.dataobject.system.Dict;
import cn.odboy.core.service.system.dto.CreateDictRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemDictService extends IService<Dict> {
    /**
     * 创建
     *
     * @param resources /
     */
    void saveDict(CreateDictRequest resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyDictById(Dict resources);

    /**
     * 删除
     *
     * @param ids /
     */
    void removeDictByIds(Set<Long> ids);

    /**
     * 导出数据
     *
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadDictExcel(List<Dict> queryAll, HttpServletResponse response) throws IOException;
}