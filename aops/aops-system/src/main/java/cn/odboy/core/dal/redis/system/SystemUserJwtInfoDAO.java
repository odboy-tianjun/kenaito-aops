package cn.odboy.core.dal.redis.system;

import cn.hutool.core.util.RandomUtil;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.service.system.dto.UserJwtVo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemUserJwtInfoDAO {
    private final RedisHelper redisHelper;

    /**
     * 返回用户缓存
     *
     * @param username 用户名
     * @return UserJwtVo
     */
    public UserJwtVo describeUserJwtModelByUsername(String username) {
        // 转小写
        username = StringUtil.lowerCase(username);
        if (StringUtil.isNotEmpty(username)) {
            // 获取数据
            return redisHelper.get(RedisKeyConst.USER_INFO + username, UserJwtVo.class);
        }
        return null;
    }

    /**
     * 添加缓存到Redis
     *
     * @param userName 用户名
     */
    @Async
    public void saveUserJwtModelByUserName(String userName, UserJwtVo user) {
        // 转小写
        userName = StringUtil.lowerCase(userName);
        if (StringUtil.isNotEmpty(userName)) {
            // 添加数据, 避免数据同时过期（2小时左右）
            long time = 7200 + RandomUtil.randomInt(900, 1800);
            redisHelper.set(RedisKeyConst.USER_INFO + userName, user, time);
        }
    }

    /**
     * 清理用户缓存信息
     * 用户信息变更时
     *
     * @param userName 用户名
     */
    @Async
    public void cleanUserJwtModelCacheByUsername(String userName) {
        // 转小写
        userName = StringUtil.lowerCase(userName);
        if (StringUtil.isNotEmpty(userName)) {
            // 清除数据
            redisHelper.del(RedisKeyConst.USER_INFO + userName);
        }
    }
}
