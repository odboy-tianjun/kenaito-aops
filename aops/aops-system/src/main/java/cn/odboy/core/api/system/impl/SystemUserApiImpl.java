package cn.odboy.core.api.system.impl;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemUserApi;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.service.system.dto.QueryUserRequest;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SystemUserApiImpl implements SystemUserApi {
    private final UserMapper userMapper;
    private final RedisHelper redisHelper;

    @Override
    public PageResult<User> describeUserPage(QueryUserRequest criteria, Page<Object> page) {
        criteria.setOffset(page.offset());
        List<User> users = userMapper.queryUserPageByArgs(criteria, PageUtil.getCount(userMapper)).getRecords();
        Long total = userMapper.getUserCountByArgs(criteria);
        return PageUtil.toPage(users, total);
    }

    @Override
    public List<User> describeUserList(QueryUserRequest criteria) {
        return userMapper.queryUserPageByArgs(criteria, PageUtil.getCount(userMapper)).getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User describeUserById(long id) {
        String key = SystemRedisKey.USER_ID + id;
        User user = redisHelper.get(key, User.class);
        if (user == null) {
            user = userMapper.selectById(id);
            redisHelper.set(key, user, 1, TimeUnit.DAYS);
        }
        return user;
    }

    @Override
    public User describeUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }
}
