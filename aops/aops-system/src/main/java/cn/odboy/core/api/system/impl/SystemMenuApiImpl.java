package cn.odboy.core.api.system.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.odboy.core.api.system.SystemMenuApi;
import cn.odboy.core.api.system.SystemRoleApi;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.Menu;
import cn.odboy.core.dal.model.MenuResponse;
import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.dal.mysql.system.MenuMapper;
import cn.odboy.core.service.system.SystemRoleService;
import cn.odboy.core.service.system.dto.MenuMetaVo;
import cn.odboy.core.service.system.dto.QueryMenuRequest;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.ClassUtil;
import cn.odboy.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemMenuApiImpl implements SystemMenuApi {
    private final MenuMapper menuMapper;
    private final SystemRoleApi systemRoleApi;
    private final SystemRoleService systemRoleService;
    private final RedisHelper redisHelper;

    @Override
    public List<Menu> describeMenuList(QueryMenuRequest criteria, Boolean isQuery) throws Exception {
        if (Boolean.TRUE.equals(isQuery)) {
            criteria.setPidIsNull(true);
            List<Field> fields = ClassUtil.getAllFields(criteria.getClass(), new ArrayList<>());
            for (Field field : fields) {
                //设置对象的访问权限，保证对private的属性的访问
                field.setAccessible(true);
                Object val = field.get(criteria);
                if ("pidIsNull".equals(field.getName())) {
                    continue;
                }
                // 如果有查询条件，则不指定pidIsNull
                if (ObjectUtil.isNotNull(val)) {
                    criteria.setPidIsNull(null);
                    break;
                }
            }
        }
        return menuMapper.queryMenuListByArgs(criteria);
    }

    @Override
    public Menu describeMenuById(long id) {
        String key = SystemRedisKey.MENU_ID + id;
        Menu menu = redisHelper.get(key, Menu.class);
        if (menu == null) {
            menu = menuMapper.selectById(id);
            redisHelper.set(key, menu, 1, TimeUnit.DAYS);
        }
        return menu;
    }

    /**
     * 用户角色改变时需清理缓存
     *
     * @param currentUserId /
     * @return /
     */
    @Override
    public List<Menu> describeMenuListByUserId(Long currentUserId) {
        String key = SystemRedisKey.MENU_USER + currentUserId;
        List<Menu> menus = redisHelper.getList(key, Menu.class);
        if (CollUtil.isEmpty(menus)) {
            List<Role> roles = systemRoleApi.describeRoleListByUsersId(currentUserId);
            Set<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
            menus = new ArrayList<>(menuMapper.queryMenuSetByRoleIdsAndType(roleIds, 2));
            redisHelper.set(key, menus, 1, TimeUnit.DAYS);
        }
        return menus;
    }
    @Override
    public Set<Menu> describeChildMenuSet(List<Menu> menuList, Set<Menu> menuSet) {
        for (Menu menu : menuList) {
            menuSet.add(menu);
            List<Menu> menus = menuMapper.queryMenuListByPidOrderByMenuSort(menu.getId());
            if (CollUtil.isNotEmpty(menus)) {
                describeChildMenuSet(menus, menuSet);
            }
        }
        return menuSet;
    }

    @Override
    public List<Menu> describeMenuListByPid(Long pid) {
        List<Menu> menus;
        if (pid != null && !pid.equals(0L)) {
            menus = menuMapper.queryMenuListByPidOrderByMenuSort(pid);
        } else {
            menus = menuMapper.queryMenuListByPidIsNullOrderByMenuSort();
        }
        return menus;
    }

    @Override
    public List<Menu> describeSuperiorMenuList(Menu menu, List<Menu> menus) {
        if (menu.getPid() == null) {
            menus.addAll(menuMapper.queryMenuListByPidIsNullOrderByMenuSort());
            return menus;
        }
        menus.addAll(menuMapper.queryMenuListByPidOrderByMenuSort(menu.getPid()));
        return describeSuperiorMenuList(describeMenuById(menu.getPid()), menus);
    }

    @Override
    public List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> trees = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (Menu menu : menus) {
            if (menu.getPid() == null) {
                trees.add(menu);
            }
            for (Menu it : menus) {
                if (menu.getId().equals(it.getPid())) {
                    if (menu.getChildren() == null) {
                        menu.setChildren(new ArrayList<>());
                    }
                    menu.getChildren().add(it);
                    ids.add(it.getId());
                }
            }
        }
        if (CollUtil.isNotEmpty(trees)) {
            trees = menus.stream().filter(s -> !ids.contains(s.getId())).collect(Collectors.toList());
        }
        return trees;
    }

    @Override
    public List<MenuResponse> buildMenuResponse(List<Menu> menus) {
        List<MenuResponse> list = new LinkedList<>();
        menus.forEach(menu -> {
                    if (menu != null) {
                        List<Menu> menuList = menu.getChildren();
                        MenuResponse menuResponse = new MenuResponse();
                        menuResponse.setName(ObjectUtil.isNotEmpty(menu.getComponentName()) ? menu.getComponentName() : menu.getTitle());
                        // 一级目录需要加斜杠，不然会报警告
                        menuResponse.setPath(menu.getPid() == null ? "/" + menu.getPath() : menu.getPath());
                        menuResponse.setHidden(menu.getHidden());
                        // 如果不是外链
                        if (!menu.getIFrame()) {
                            if (menu.getPid() == null) {
                                menuResponse.setComponent(StringUtil.isEmpty(menu.getComponent()) ? "Layout" : menu.getComponent());
                                // 如果不是一级菜单，并且菜单类型为目录，则代表是多级菜单
                            } else if (menu.getType() == 0) {
                                menuResponse.setComponent(StringUtil.isEmpty(menu.getComponent()) ? "ParentView" : menu.getComponent());
                            } else if (StringUtil.isNoneBlank(menu.getComponent())) {
                                menuResponse.setComponent(menu.getComponent());
                            }
                        }
                        menuResponse.setMeta(new MenuMetaVo(menu.getTitle(), menu.getIcon(), !menu.getCache()));
                        if (CollectionUtil.isNotEmpty(menuList)) {
                            menuResponse.setAlwaysShow(true);
                            menuResponse.setRedirect("noredirect");
                            menuResponse.setChildren(buildMenuResponse(menuList));
                            // 处理是一级菜单并且没有子菜单的情况
                        } else if (menu.getPid() == null) {
                            MenuResponse menuResponse1 = getMenuVo(menu, menuResponse);
                            menuResponse.setName(null);
                            menuResponse.setMeta(null);
                            menuResponse.setComponent("Layout");
                            List<MenuResponse> list1 = new ArrayList<>();
                            list1.add(menuResponse1);
                            menuResponse.setChildren(list1);
                        }
                        list.add(menuResponse);
                    }
                }
        );
        return list;
    }

    /**
     * 获取 MenuResponse
     *
     * @param menu         /
     * @param menuResponse /
     * @return /
     */
    private static MenuResponse getMenuVo(Menu menu, MenuResponse menuResponse) {
        MenuResponse menuResponse1 = new MenuResponse();
        menuResponse1.setMeta(menuResponse.getMeta());
        // 非外链
        if (!menu.getIFrame()) {
            menuResponse1.setPath("index");
            menuResponse1.setName(menuResponse.getName());
            menuResponse1.setComponent(menuResponse.getComponent());
        } else {
            menuResponse1.setPath(menu.getPath());
        }
        return menuResponse1;
    }
}
