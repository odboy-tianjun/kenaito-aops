package cn.odboy.core.api.system.impl;

import cn.hutool.core.collection.CollUtil;
import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemRoleApi;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.Menu;
import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.dal.mysql.system.RoleMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.service.system.dto.QueryRoleRequest;
import cn.odboy.core.service.system.dto.RoleCodeVo;
import cn.odboy.exception.BadRequestException;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.PageUtil;
import cn.odboy.util.StringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemRoleApiImpl implements SystemRoleApi {
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final RedisHelper redisHelper;

    @Override
    public List<Role> describeRoleList() {
        return roleMapper.queryRoleList();
    }

    @Override
    public List<Role> describeRoleList(QueryRoleRequest criteria) {
        return roleMapper.queryRoleListByArgs(criteria);
    }

    @Override
    public PageResult<Role> describeRolePage(QueryRoleRequest criteria, Page<Object> page) {
        criteria.setOffset(page.offset());
        List<Role> roles = roleMapper.queryRoleListByArgs(criteria);
        Long total = roleMapper.getRoleCountByArgs(criteria);
        return PageUtil.toPage(roles, total);
    }

    @Override
    public Role describeRoleById(long id) {
        String key = SystemRedisKey.ROLE_ID + id;
        Role role = redisHelper.get(key, Role.class);
        if (role == null) {
            role = roleMapper.selectById(id);
            redisHelper.set(key, role, 1, TimeUnit.DAYS);
        }
        return role;
    }

    @Override
    public List<Role> describeRoleListByUsersId(Long userId) {
        String key = SystemRedisKey.ROLE_USER + userId;
        List<Role> roles = redisHelper.getList(key, Role.class);
        if (CollUtil.isEmpty(roles)) {
            roles = roleMapper.queryRoleListByUserId(userId);
            redisHelper.set(key, roles, 1, TimeUnit.DAYS);
        }
        return roles;
    }

    @Override
    public Integer describeDeptLevelByRoles(Set<Role> roles) {
        if (CollUtil.isEmpty(roles)) {
            return Integer.MAX_VALUE;
        }
        Set<Role> roleSet = new HashSet<>();
        for (Role role : roles) {
            roleSet.add(describeRoleById(role.getId()));
        }
        return Collections.min(roleSet.stream().map(Role::getLevel).collect(Collectors.toList()));
    }

    @Override
    public List<RoleCodeVo> buildUserRolePermissions(User user) {
        String key = SystemRedisKey.ROLE_AUTH + user.getId();
        List<RoleCodeVo> authorityList = redisHelper.getList(key, RoleCodeVo.class);
        if (CollUtil.isEmpty(authorityList)) {
            Set<String> permissions = new HashSet<>();
            // 如果是管理员直接返回
            if (user.getIsAdmin()) {
                permissions.add("admin");
                return permissions.stream().map(RoleCodeVo::new)
                        .collect(Collectors.toList());
            }
            List<Role> roles = roleMapper.queryRoleListByUserId(user.getId());
            permissions = roles.stream().flatMap(role -> role.getMenus().stream())
                    .map(Menu::getPermission)
                    .filter(StringUtil::isNotBlank).collect(Collectors.toSet());
            authorityList = permissions.stream().map(RoleCodeVo::new)
                    .collect(Collectors.toList());
            redisHelper.set(key, authorityList, 1, TimeUnit.HOURS);
        }
        return authorityList;
    }

    @Override
    public void verifyBindRelationByIds(Set<Long> ids) {
        if (userMapper.getUserCountByRoleIds(ids) > 0) {
            throw new BadRequestException("所选角色存在用户关联，请解除关联再试！");
        }
    }

    @Override
    public List<Role> describeRoleListByMenuId(Long menuId) {
        return roleMapper.queryRoleListByMenuId(menuId);
    }
}
