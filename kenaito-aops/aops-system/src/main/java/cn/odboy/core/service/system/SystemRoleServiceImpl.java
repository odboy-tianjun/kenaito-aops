package cn.odboy.core.service.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.exception.EntityExistException;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.PageUtil;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.controller.system.vo.RoleCodeVo;
import cn.odboy.core.dal.dataobject.system.MenuDO;
import cn.odboy.core.dal.dataobject.system.RoleDO;
import cn.odboy.core.dal.dataobject.system.UserDO;
import cn.odboy.core.dal.mysql.system.RoleDeptMapper;
import cn.odboy.core.dal.mysql.system.RoleMapper;
import cn.odboy.core.dal.mysql.system.RoleMenuMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.dal.redis.system.SystemUserJwtInfoDAO;
import cn.odboy.core.service.system.dto.RoleCreateArgs;
import cn.odboy.core.service.system.dto.RoleQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemRoleServiceImpl extends ServiceImpl<RoleMapper, RoleDO> implements SystemRoleService {
    private final RoleMapper roleMapper;
    private final RoleDeptMapper roleDeptMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserMapper userMapper;
    private final SystemUserJwtInfoDAO systemUserJwtInfoDAO;
    private final RedisHelper redisHelper;

    @Override
    public List<RoleDO> describeRoleList() {
        return roleMapper.queryRoleList();
    }

    @Override
    public List<RoleDO> describeRoleList(RoleQueryArgs args) {
        return roleMapper.queryRoleListByArgs(args);
    }

    @Override
    public PageResult<RoleDO> describeRolePage(RoleQueryArgs args, Page<Object> page) {
        args.setOffset(page.offset());
        List<RoleDO> roleDOS = roleMapper.queryRoleListByArgs(args);
        Long total = roleMapper.getRoleCountByArgs(args);
        return PageUtil.toPage(roleDOS, total);
    }

    @Override
    public RoleDO describeRoleById(long id) {
        String key = RedisKeyConst.ROLE_ID + id;
        RoleDO roleDO = redisHelper.get(key, RoleDO.class);
        if (roleDO == null) {
            roleDO = roleMapper.selectById(id);
            redisHelper.set(key, roleDO, 1, TimeUnit.DAYS);
        }
        return roleDO;
    }

    @Override
    public List<RoleDO> describeRoleListByUsersId(Long userId) {
        String key = RedisKeyConst.ROLE_USER + userId;
        List<RoleDO> roleDOS = redisHelper.getList(key, RoleDO.class);
        if (CollUtil.isEmpty(roleDOS)) {
            roleDOS = roleMapper.queryRoleListByUserId(userId);
            redisHelper.set(key, roleDOS, 1, TimeUnit.DAYS);
        }
        return roleDOS;
    }

    @Override
    public Integer describeDeptLevelByRoles(Set<RoleDO> roleDOS) {
        if (CollUtil.isEmpty(roleDOS)) {
            return Integer.MAX_VALUE;
        }
        Set<RoleDO> roleDOSet = new HashSet<>();
        for (RoleDO roleDO : roleDOS) {
            roleDOSet.add(describeRoleById(roleDO.getId()));
        }
        return Collections.min(roleDOSet.stream().map(RoleDO::getLevel).collect(Collectors.toList()));
    }

    @Override
    public List<RoleCodeVo> buildUserRolePermissions(UserDO userDO) {
        String key = RedisKeyConst.ROLE_AUTH + userDO.getId();
        List<RoleCodeVo> authorityList = redisHelper.getList(key, RoleCodeVo.class);
        if (CollUtil.isEmpty(authorityList)) {
            Set<String> permissions = new HashSet<>();
            // 如果是管理员直接返回
            if (userDO.getIsAdmin()) {
                permissions.add("admin");
                return permissions.stream().map(RoleCodeVo::new)
                        .collect(Collectors.toList());
            }
            List<RoleDO> roleDOS = roleMapper.queryRoleListByUserId(userDO.getId());
            permissions = roleDOS.stream().flatMap(role -> role.getMenus().stream())
                    .map(MenuDO::getPermission)
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
    public List<RoleDO> describeRoleListByMenuId(Long menuId) {
        return roleMapper.queryRoleListByMenuId(menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(RoleCreateArgs args) {
        if (roleMapper.getRoleByName(args.getName()) != null) {
            throw new EntityExistException(RoleDO.class, "name", args.getName());
        }
        save(BeanUtil.copyProperties(args, RoleDO.class));
        // 判断是否有部门数据，若有，则需创建关联
        if (CollectionUtil.isNotEmpty(args.getDepts())) {
            roleDeptMapper.insertBatchWithRoleId(args.getDepts(), args.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyRoleById(RoleDO resources) {
        RoleDO roleDO = getById(resources.getId());
        RoleDO roleDO1 = roleMapper.getRoleByName(resources.getName());
        if (roleDO1 != null && !roleDO1.getId().equals(roleDO.getId())) {
            throw new EntityExistException(RoleDO.class, "name", resources.getName());
        }
        roleDO.setName(resources.getName());
        roleDO.setDescription(resources.getDescription());
        roleDO.setDataScope(resources.getDataScope());
        roleDO.setDepts(resources.getDepts());
        roleDO.setLevel(resources.getLevel());
        // 更新
        saveOrUpdate(roleDO);
        // 删除关联部门数据
        roleDeptMapper.deleteByRoleId(resources.getId());
        // 判断是否有部门数据，若有，则需更新关联
        if (CollectionUtil.isNotEmpty(resources.getDepts())) {
            roleDeptMapper.insertBatchWithRoleId(resources.getDepts(), resources.getId());
        }
        // 更新相关缓存
        delCaches(roleDO.getId(), null);
    }

    @Override
    public void modifyBindMenuById(RoleDO roleDO) {
        List<UserDO> userDOS = userMapper.queryUserListByRoleId(roleDO.getId());
        // 更新菜单
        roleMenuMapper.deleteByRoleId(roleDO.getId());
        // 判断是否为空
        if (CollUtil.isNotEmpty(roleDO.getMenus())) {
            roleMenuMapper.insertBatchWithRoleId(roleDO.getMenus(), roleDO.getId());
        }
        // 更新缓存
        delCaches(roleDO.getId(), userDOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoleByIds(Set<Long> ids) {
        for (Long id : ids) {
            // 更新相关缓存
            delCaches(id, null);
        }
        removeBatchByIds(ids);
        // 删除角色部门关联数据、角色菜单关联数据
        roleDeptMapper.deleteByRoleIds(ids);
        roleMenuMapper.deleteByRoleIds(ids);
    }

    @Override
    public void downloadRoleExcel(List<RoleDO> roleDOS, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RoleDO roleDO : roleDOS) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("角色名称", roleDO.getName());
            map.put("角色级别", roleDO.getLevel());
            map.put("描述", roleDO.getDescription());
            map.put("创建日期", roleDO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 清理缓存
     *
     * @param id /
     */
    public void delCaches(Long id, List<UserDO> userDOS) {
        userDOS = CollectionUtil.isEmpty(userDOS) ? userMapper.queryUserListByRoleId(id) : userDOS;
        if (CollectionUtil.isNotEmpty(userDOS)) {
            userDOS.forEach(item -> systemUserJwtInfoDAO.cleanUserJwtModelCacheByUsername(item.getUsername()));
            Set<Long> userIds = userDOS.stream().map(UserDO::getId).collect(Collectors.toSet());
            redisHelper.delByKeys(RedisKeyConst.DATA_USER, userIds);
            redisHelper.delByKeys(RedisKeyConst.MENU_USER, userIds);
            redisHelper.delByKeys(RedisKeyConst.ROLE_AUTH, userIds);
            redisHelper.delByKeys(RedisKeyConst.ROLE_USER, userIds);
        }
        redisHelper.del(RedisKeyConst.ROLE_ID + id);
    }
}
