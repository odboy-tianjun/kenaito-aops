package cn.odboy.core.api.system;

import cn.odboy.base.PageResult;
import cn.odboy.core.dal.dataobject.system.DictDetail;
import cn.odboy.core.service.system.dto.QueryDictDetailRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface SystemDictDetailApi {
    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<DictDetail> describeDictDetailPage(QueryDictDetailRequest criteria, Page<Object> page);

    /**
     * 根据字典名称获取字典详情
     *
     * @param name 字典名称
     * @return /
     */
    List<DictDetail> describeDictDetailListByName(String name);
}
