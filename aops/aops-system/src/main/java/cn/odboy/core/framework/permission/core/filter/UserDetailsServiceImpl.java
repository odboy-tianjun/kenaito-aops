package cn.odboy.core.framework.permission.core.filter;

import cn.odboy.common.exception.BadRequestException;
import cn.odboy.core.dal.dataobject.system.UserDO;
import cn.odboy.core.dal.redis.system.SystemUserJwtInfoDAO;
import cn.odboy.core.service.system.SystemDataService;
import cn.odboy.core.service.system.SystemRoleService;
import cn.odboy.core.service.system.SystemUserService;
import cn.odboy.core.controller.system.vo.RoleCodeVo;
import cn.odboy.core.controller.system.vo.UserJwtVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * SpringSecurity框架加载用户实现
 *
 * @author odboy
 * @date 2025-05-09
 */
@Slf4j
@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final SystemRoleService systemRoleService;
    private final SystemUserService systemUserService;
    private final SystemDataService systemDataService;
    private final SystemUserJwtInfoDAO systemUserJwtInfoDAO;

    @Override
    public UserJwtVo loadUserByUsername(String username) {
        UserJwtVo userJwtVo = systemUserJwtInfoDAO.describeUserJwtModelByUsername(username);
        if (userJwtVo == null) {
            UserDO userDO = systemUserService.describeUserByUsername(username);
            if (userDO == null) {
                throw new BadRequestException("用户不存在");
            } else {
                if (!userDO.getEnabled()) {
                    throw new BadRequestException("账号未激活！");
                }
                // 获取用户的权限
                List<RoleCodeVo> authorities = systemRoleService.buildUserRolePermissions(userDO);
                // 初始化JwtUserDto
                userJwtVo = new UserJwtVo(userDO, systemDataService.describeDeptIdListByUserIdWithDeptId(userDO), authorities);
                // 添加缓存数据
                systemUserJwtInfoDAO.saveUserJwtModelByUserName(username, userJwtVo);
            }
        }
        return userJwtVo;
    }
}
