package cn.odboy.core.service.system;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.odboy.common.util.ClassUtil;
import cn.odboy.core.controller.system.vo.MenuResponse;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.constant.TransferProtocolConst;
import cn.odboy.core.dal.dataobject.system.Menu;
import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.dal.mysql.system.MenuMapper;
import cn.odboy.core.dal.mysql.system.RoleMenuMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.exception.EntityExistException;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.service.system.dto.MenuMetaVo;
import cn.odboy.core.service.system.dto.QueryMenuRequest;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SystemMenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements SystemMenuService {
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserMapper userMapper;
    private final SystemRoleService systemRoleService;
    private final RedisHelper redisHelper;

    private static final String YES_STR = "是";
    private static final String NO_STR = "否";

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
        String key = RedisKeyConst.MENU_ID + id;
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
        String key = RedisKeyConst.MENU_USER + currentUserId;
        List<Menu> menus = redisHelper.getList(key, Menu.class);
        if (CollUtil.isEmpty(menus)) {
            List<Role> roles = systemRoleService.describeRoleListByUsersId(currentUserId);
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
        redisHelper.del(RedisKeyConst.MENU_ID + id);
        redisHelper.delByKeys(RedisKeyConst.MENU_USER, users.stream().map(User::getId).collect(Collectors.toSet()));
        // 清除 Role 缓存
        List<Role> roles = systemRoleService.describeRoleListByMenuId(id);
        redisHelper.delByKeys(RedisKeyConst.ROLE_ID, roles.stream().map(Role::getId).collect(Collectors.toSet()));
    }

}
