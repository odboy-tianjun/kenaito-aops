package cn.odboy.core.api.system.impl;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemDictApi;
import cn.odboy.core.dal.dataobject.system.Dict;
import cn.odboy.core.dal.mysql.system.DictMapper;
import cn.odboy.core.service.system.dto.QueryDictRequest;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemDictApiImpl implements SystemDictApi {
    private final DictMapper dictMapper;
    @Override
    public PageResult<Dict> describeDictPage(QueryDictRequest criteria, Page<Object> page) {
        IPage<Dict> dicts = dictMapper.queryDictPageByArgs(criteria, page);
        return PageUtil.toPage(dicts);
    }

    @Override
    public List<Dict> describeDictList(QueryDictRequest criteria) {
        return dictMapper.queryDictPageByArgs(criteria, PageUtil.getCount(dictMapper)).getRecords();
    }
}
