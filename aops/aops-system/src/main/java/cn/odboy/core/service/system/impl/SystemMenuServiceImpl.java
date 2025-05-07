package cn.odboy.core.service.system.impl;

import cn.odboy.core.api.system.SystemRoleApi;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.constant.TransferProtocolConst;
import cn.odboy.core.dal.dataobject.system.Menu;
import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.dal.mysql.system.MenuMapper;
import cn.odboy.core.dal.mysql.system.RoleMenuMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.service.system.SystemMenuService;
import cn.odboy.exception.BadRequestException;
import cn.odboy.exception.EntityExistException;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.FileUtil;
import cn.odboy.util.StringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SystemMenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements SystemMenuService {
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserMapper userMapper;
    private final SystemRoleApi systemRoleApi;
    private final RedisHelper redisHelper;

    private static final String YES_STR = "是";
    private static final String NO_STR = "否";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(Menu resources) {
        if (menuMapper.getMenuByTitle(resources.getTitle()) != null) {
            throw new EntityExistException(Menu.class, "title", resources.getTitle());
        }
        if (StringUtil.isNotBlank(resources.getComponentName())) {
            if (menuMapper.getMenuByComponentName(resources.getComponentName()) != null) {
                throw new EntityExistException(Menu.class, "componentName", resources.getComponentName());
            }
        }
        if (Long.valueOf(0L).equals(resources.getPid())) {
            resources.setPid(null);
        }
        if (resources.getIFrame()) {
            if (!(resources.getPath().toLowerCase().startsWith(TransferProtocolConst.PREFIX_HTTP) || resources.getPath().toLowerCase().startsWith(TransferProtocolConst.PREFIX_HTTPS))) {
                throw new BadRequestException(TransferProtocolConst.PREFIX_HTTPS_BAD_REQUEST);
            }
        }
        save(resources);
        // 计算子节点数目
        resources.setSubCount(0);
        // 更新父节点菜单数目
        updateSubCnt(resources.getPid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyMenuById(Menu resources) {
        if (resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        Menu menu = getById(resources.getId());
        if (resources.getIFrame()) {
            if (!(resources.getPath().toLowerCase().startsWith(TransferProtocolConst.PREFIX_HTTP) || resources.getPath().toLowerCase().startsWith(TransferProtocolConst.PREFIX_HTTPS))) {
                throw new BadRequestException(TransferProtocolConst.PREFIX_HTTPS_BAD_REQUEST);
            }
        }
        Menu menu1 = menuMapper.getMenuByTitle(resources.getTitle());

        if (menu1 != null && !menu1.getId().equals(menu.getId())) {
            throw new EntityExistException(Menu.class, "title", resources.getTitle());
        }

        if (resources.getPid().equals(0L)) {
            resources.setPid(null);
        }

        // 记录的父节点ID
        Long oldPid = menu.getPid();
        Long newPid = resources.getPid();

        if (StringUtil.isNotBlank(resources.getComponentName())) {
            menu1 = menuMapper.getMenuByComponentName(resources.getComponentName());
            if (menu1 != null && !menu1.getId().equals(menu.getId())) {
                throw new EntityExistException(Menu.class, "componentName", resources.getComponentName());
            }
        }
        menu.setTitle(resources.getTitle());
        menu.setComponent(resources.getComponent());
        menu.setPath(resources.getPath());
        menu.setIcon(resources.getIcon());
        menu.setIFrame(resources.getIFrame());
        menu.setPid(resources.getPid());
        menu.setMenuSort(resources.getMenuSort());
        menu.setCache(resources.getCache());
        menu.setHidden(resources.getHidden());
        menu.setComponentName(resources.getComponentName());
        menu.setPermission(resources.getPermission());
        menu.setType(resources.getType());
        saveOrUpdate(menu);
        // 计算父级菜单节点数目
        updateSubCnt(oldPid);
        updateSubCnt(newPid);
        // 清理缓存
        delCaches(resources.getId());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMenuByIds(Set<Menu> menuSet) {
        for (Menu menu : menuSet) {
            // 清理缓存
            delCaches(menu.getId());
            roleMenuMapper.deleteByMenuId(menu.getId());
            menuMapper.deleteById(menu.getId());
            updateSubCnt(menu.getPid());
        }
    }


    @Override
    public void downloadMenuExcel(List<Menu> menus, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Menu menu : menus) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("菜单标题", menu.getTitle());
            map.put("菜单类型", menu.getType() == null ? "目录" : menu.getType() == 1 ? "菜单" : "按钮");
            map.put("权限标识", menu.getPermission());
            map.put("外链菜单", menu.getIFrame() ? YES_STR : NO_STR);
            map.put("菜单可见", menu.getHidden() ? NO_STR : YES_STR);
            map.put("是否缓存", menu.getCache() ? YES_STR : NO_STR);
            map.put("创建日期", menu.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    private void updateSubCnt(Long menuId) {
        if (menuId != null) {
            int count = menuMapper.getMenuCountByPid(menuId);
            menuMapper.updateSubCntByMenuId(count, menuId);
        }
    }

    /**
     * 清理缓存
     *
     * @param id 菜单ID
     */
    public void delCaches(Long id) {
        List<User> users = userMapper.queryUserListByMenuId(id);
        redisHelper.del(SystemRedisKey.MENU_ID + id);
        redisHelper.delByKeys(SystemRedisKey.MENU_USER, users.stream().map(User::getId).collect(Collectors.toSet()));
        // 清除 Role 缓存
        List<Role> roles = systemRoleApi.describeRoleListByMenuId(id);
        redisHelper.delByKeys(SystemRedisKey.ROLE_ID, roles.stream().map(Role::getId).collect(Collectors.toSet()));
    }

}
