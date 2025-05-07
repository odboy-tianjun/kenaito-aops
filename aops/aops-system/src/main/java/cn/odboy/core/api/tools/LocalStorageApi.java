package cn.odboy.core.api.tools;

import cn.odboy.base.PageResult;
import cn.odboy.core.dal.dataobject.tools.LocalStorage;
import cn.odboy.core.service.tools.dto.QueryLocalStorageRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface LocalStorageApi {

    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<LocalStorage> describeLocalStoragePage(QueryLocalStorageRequest criteria, Page<Object> page);

    /**
     * 查询全部数据
     *
     * @param criteria 条件
     * @return /
     */
    List<LocalStorage> describeLocalStorageList(QueryLocalStorageRequest criteria);
}
