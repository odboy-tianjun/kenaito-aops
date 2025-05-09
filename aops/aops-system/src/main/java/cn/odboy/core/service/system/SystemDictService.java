package cn.odboy.core.service.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.service.system.dto.CreateDictRequest;
import cn.odboy.core.dal.dataobject.system.Dict;
import cn.odboy.core.service.system.dto.QueryDictRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemDictService extends IService<Dict> {
    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<Dict> describeDictPage(QueryDictRequest criteria, Page<Object> page);

    /**
     * 查询全部数据
     *
     * @param criteria /
     * @return /
     */
    List<Dict> describeDictList(QueryDictRequest criteria);
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