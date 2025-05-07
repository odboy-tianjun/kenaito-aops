package cn.odboy.core.api.system;

import cn.odboy.base.PageResult;
import cn.odboy.core.dal.dataobject.system.Dict;
import cn.odboy.core.service.system.dto.QueryDictRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface SystemDictApi {
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
}
