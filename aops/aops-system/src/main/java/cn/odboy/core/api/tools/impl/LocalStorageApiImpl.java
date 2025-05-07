package cn.odboy.core.api.tools.impl;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.tools.LocalStorageApi;
import cn.odboy.core.dal.dataobject.tools.LocalStorage;
import cn.odboy.core.dal.mysql.tools.LocalStorageMapper;
import cn.odboy.core.service.tools.dto.QueryLocalStorageRequest;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalStorageApiImpl implements LocalStorageApi {
    private final LocalStorageMapper localStorageMapper;

    @Override
    public PageResult<LocalStorage> describeLocalStoragePage(QueryLocalStorageRequest criteria, Page<Object> page) {
        return PageUtil.toPage(localStorageMapper.queryLocalStoragePageByArgs(criteria, page));
    }

    @Override
    public List<LocalStorage> describeLocalStorageList(QueryLocalStorageRequest criteria) {
        return localStorageMapper.queryLocalStoragePageByArgs(criteria, PageUtil.getCount(localStorageMapper)).getRecords();
    }
}
