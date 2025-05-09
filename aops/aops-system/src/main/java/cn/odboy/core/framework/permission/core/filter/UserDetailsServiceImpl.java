package cn.odboy.core.framework.permission.core.filter;

import cn.odboy.core.api.system.api.SystemDataApi;
import cn.odboy.core.api.system.api.SystemRoleApi;
import cn.odboy.core.api.system.api.SystemUserApi;
import cn.odboy.core.dal.redis.system.SystemUserJwtInfoDAO;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.service.system.dto.RoleCodeVo;
import cn.odboy.core.service.system.dto.UserJwtVo;
import cn.odboy.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final SystemRoleApi systemRoleApi;
    private final SystemUserApi systemUserApi;
    private final SystemDataApi systemDataApi;
    private final SystemUserJwtInfoDAO systemUserJwtInfoDAO;

    @Override
    public UserJwtVo loadUserByUsername(String username) {
        UserJwtVo userJwtVo = systemUserJwtInfoDAO.describeUserJwtModelByUsername(username);
        if (userJwtVo == null) {
            User user = systemUserApi.describeUserByUsername(username);
            if (user == null) {
                throw new BadRequestException("用户不存在");
            } else {
                if (!user.getEnabled()) {
                    throw new BadRequestException("账号未激活！");
                }
                // 获取用户的权限
                List<RoleCodeVo> authorities = systemRoleApi.buildUserRolePermissions(user);
                // 初始化JwtUserDto
                userJwtVo = new UserJwtVo(user, systemDataApi.describeDeptIdListByUserIdWithDeptId(user), authorities);
                // 添加缓存数据
                systemUserJwtInfoDAO.saveUserJwtModelByUserName(username, userJwtVo);
            }
        }
        return userJwtVo;
    }
}
