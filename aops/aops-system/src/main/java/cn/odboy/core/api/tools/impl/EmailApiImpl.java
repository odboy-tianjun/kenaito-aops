package cn.odboy.core.api.tools.impl;

import cn.odboy.core.api.tools.EmailApi;
import cn.odboy.core.dal.dataobject.tools.EmailConfig;
import cn.odboy.core.dal.mysql.tools.EmailConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "email")
public class EmailApiImpl implements EmailApi {
private final EmailConfigMapper emailConfigMapper;
    @Override
    @Cacheable(key = "'config'")
    public EmailConfig describeEmailConfig() {
        EmailConfig emailConfig = emailConfigMapper.selectById(1L);
        return emailConfig == null ? new EmailConfig() : emailConfig;
    }
}
