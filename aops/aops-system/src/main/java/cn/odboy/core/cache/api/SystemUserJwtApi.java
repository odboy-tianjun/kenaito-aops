package cn.odboy.core.cache.api;

import cn.odboy.core.service.system.dto.UserJwtVo;

public interface SystemUserJwtApi {
    /**
     * 返回用户缓存
     *
     * @param username 用户名
     * @return UserJwtVo
     */
    UserJwtVo describeUserJwtModelByUsername(String username);
}
