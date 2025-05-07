package cn.odboy.core.api.system.impl;

import cn.hutool.core.collection.CollUtil;
import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemDictDetailApi;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.DictDetail;
import cn.odboy.core.dal.mysql.system.DictDetailMapper;
import cn.odboy.core.service.system.dto.QueryDictDetailRequest;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SystemDictDetailApiImpl implements SystemDictDetailApi {
    private final DictDetailMapper dictDetailMapper;
    private final RedisHelper redisHelper;
    @Override
    public PageResult<DictDetail> describeDictDetailPage(QueryDictDetailRequest criteria, Page<Object> page) {
        return PageUtil.toPage(dictDetailMapper.queryDictDetailPageByArgs(criteria, page));
    }
    @Override
    public List<DictDetail> describeDictDetailListByName(String name) {
        String key = SystemRedisKey.DICT_NAME + name;
        List<DictDetail> dictDetails = redisHelper.getList(key, DictDetail.class);
        if (CollUtil.isEmpty(dictDetails)) {
            dictDetails = dictDetailMapper.queryDictDetailListByDictName(name);
            redisHelper.set(key, dictDetails, 1, TimeUnit.DAYS);
        }
        return dictDetails;
    }
}
