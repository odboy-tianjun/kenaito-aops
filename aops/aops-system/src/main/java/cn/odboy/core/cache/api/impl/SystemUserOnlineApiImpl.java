package cn.odboy.core.cache.api.impl;

import cn.odboy.base.PageResult;
import cn.odboy.core.cache.api.SystemUserOnlineApi;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.service.system.dto.UserOnlineVo;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.PageUtil;
import cn.odboy.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemUserOnlineApiImpl implements SystemUserOnlineApi {
    private final RedisHelper redisHelper;


    @Override
    public PageResult<UserOnlineVo> describeUserOnlineModelPage(String username, Pageable pageable) {
        List<UserOnlineVo> onlineUserList = describeUserOnlineModelListByUsername(username);
        List<UserOnlineVo> paging = PageUtil.softPaging(pageable.getPageNumber(), pageable.getPageSize(), onlineUserList);
        return PageUtil.toPage(paging, onlineUserList.size());
    }

    @Override
    public List<UserOnlineVo> describeUserOnlineModelListByUsername(String username) {
        String loginKey = SystemRedisKey.ONLINE_USER + (StringUtil.isBlank(username) ? "" : "*" + username);
        List<String> keys = redisHelper.scan(loginKey + "*");
        Collections.reverse(keys);
        List<UserOnlineVo> onlineUserList = new ArrayList<>();
        for (String key : keys) {
            onlineUserList.add(redisHelper.get(key, UserOnlineVo.class));
        }
        onlineUserList.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
        return onlineUserList;
    }

    @Override
    public UserOnlineVo describeUserOnlineModelByKey(String key) {
        return redisHelper.get(key, UserOnlineVo.class);
    }
}
