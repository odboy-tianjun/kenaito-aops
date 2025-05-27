package cn.odboy.core.service.system;

import cn.odboy.common.model.PageResult;
import cn.odboy.core.dal.dataobject.system.DictDO;
import cn.odboy.core.service.system.dto.DictCreateArgs;
import cn.odboy.core.service.system.dto.DictQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemDictService extends IService<DictDO> {
    /**
     * 分页查询
     *
     * @param args 条件
     * @param page 分页参数
     * @return /
     */
    PageResult<DictDO> describeDictPage(DictQueryArgs args, Page<Object> page);

    /**
     * 查询全部数据
     *
     * @param args /
     * @return /
     */
    List<DictDO> describeDictList(DictQueryArgs args);

    /**
     * 创建
     *
     * @param args /
     */
    void saveDict(DictCreateArgs args);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyDictById(DictDO resources);

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
    void downloadDictExcel(List<DictDO> queryAll, HttpServletResponse response) throws IOException;
}
