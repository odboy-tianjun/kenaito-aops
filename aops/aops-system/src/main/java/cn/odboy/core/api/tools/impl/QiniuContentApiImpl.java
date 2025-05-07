package cn.odboy.core.api.tools.impl;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.tools.QiniuContentApi;
import cn.odboy.core.dal.dataobject.tools.QiniuContent;
import cn.odboy.core.dal.mysql.tools.QiniuContentMapper;
import cn.odboy.core.service.tools.dto.QueryQiniuRequest;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "qiNiu")
public class QiniuContentApiImpl implements QiniuContentApi {
    private final QiniuContentMapper qiniuContentMapper;
    @Override
    public PageResult<QiniuContent> describeQiniuContentPage(QueryQiniuRequest criteria, Page<Object> page) {
        return PageUtil.toPage(qiniuContentMapper.queryQiniuContentPageByArgs(criteria, page));
    }

    @Override
    public List<QiniuContent> describeQiniuContentList(QueryQiniuRequest criteria) {
        return qiniuContentMapper.queryQiniuContentPageByArgs(criteria, PageUtil.getCount(qiniuContentMapper)).getRecords();
    }
}
