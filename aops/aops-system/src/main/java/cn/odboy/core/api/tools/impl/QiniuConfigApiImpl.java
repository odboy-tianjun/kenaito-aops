package cn.odboy.core.api.tools.impl;

import cn.odboy.core.api.tools.QiniuConfigApi;
import cn.odboy.core.dal.dataobject.tools.QiniuConfig;
import cn.odboy.core.dal.mysql.tools.QiniuConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "qiNiu")
public class QiniuConfigApiImpl implements QiniuConfigApi {
    private final QiniuConfigMapper qiniuConfigMapper;
    @Override
    @Cacheable(key = "'config'")
    public QiniuConfig describeQiniuConfig() {
        QiniuConfig qiniuConfig = qiniuConfigMapper.selectById(1L);
        return qiniuConfig == null ? new QiniuConfig() : qiniuConfig;
    }
}
