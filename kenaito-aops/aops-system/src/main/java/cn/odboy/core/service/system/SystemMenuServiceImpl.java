package cn.odboy.core.service.system;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.exception.EntityExistException;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.ClassUtil;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.constant.TransferProtocolConst;
import cn.odboy.core.controller.system.vo.MenuMetaVo;
import cn.odboy.core.controller.system.vo.MenuVo;
import cn.odboy.core.dal.dataobject.system.MenuDO;
import cn.odboy.core.dal.dataobject.system.RoleDO;
import cn.odboy.core.dal.dataobject.system.UserDO;
import cn.odboy.core.dal.mysql.system.MenuMapper;
import cn.odboy.core.dal.mysql.system.RoleMenuMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.service.system.dto.MenuQueryArgs;
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
public class SystemMenuServiceImpl extends ServiceImpl<MenuMapper, MenuDO> implements SystemMenuService {
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserMapper userMapper;
    private final SystemRoleService systemRoleService;
    private final RedisHelper redisHelper;

    private static final String YES_STR = "是";
    private static final String NO_STR = "否";

    @Override
    public List<MenuDO> describeMenuList(MenuQueryArgs args, Boolean isQuery) throws Exception {
        if (Boolean.TRUE.equals(isQuery)) {
            args.setPidIsNull(true);
            List<Field> fields = ClassUtil.getAllFields(args.getClass(), new ArrayList<>());
            for (Field field : fields) {
                //设置对象的访问权限，保证对private的属性的访问
                field.setAccessible(true);
                Object val = field.get(args);
                if ("pidIsNull".equals(field.getName())) {
                    continue;
                }
                // 如果有查询条件，则不指定pidIsNull
                if (ObjectUtil.isNotNull(val)) {
                    args.setPidIsNull(null);
                    break;
                }
            }
        }
        return menuMapper.queryMenuListByArgs(args);
    }

    @Override
    public MenuDO describeMenuById(long id) {
        String key = RedisKeyConst.MENU_ID + id;
        MenuDO menuDO = redisHelper.get(key, MenuDO.class);
        if (menuDO == null) {
            menuDO = menuMapper.selectById(id);
            redisHelper.set(key, menuDO, 1, TimeUnit.DAYS);
        }
        return menuDO;
    }

    /**
     * 用户角色改变时需清理缓存
     *
     * @param currentUserId /
     * @return /
     */
    @Override
    public List<MenuDO> describeMenuListByUserId(Long currentUserId) {
        String key = RedisKeyConst.MENU_USER + currentUserId;
        List<MenuDO> menuDOS = redisHelper.getList(key, MenuDO.class);
        if (CollUtil.isEmpty(menuDOS)) {
            List<RoleDO> roleDOS = systemRoleService.describeRoleListByUsersId(currentUserId);
            Set<Long> roleIds = roleDOS.stream().map(RoleDO::getId).collect(Collectors.toSet());
            menuDOS = new ArrayList<>(menuMapper.queryMenuSetByRoleIdsAndType(roleIds, 2));
            redisHelper.set(key, menuDOS, 1, TimeUnit.DAYS);
        }
        return menuDOS;
    }

    @Override
    public Set<MenuDO> describeChildMenuSet(List<MenuDO> menuDOList, Set<MenuDO> menuDOSet) {
        for (MenuDO menuDO : menuDOList) {
            menuDOSet.add(menuDO);
            List<MenuDO> menuDOS = menuMapper.queryMenuListByPidOrderByMenuSort(menuDO.getId());
            if (CollUtil.isNotEmpty(menuDOS)) {
                describeChildMenuSet(menuDOS, menuDOSet);
            }
        }
        return menuDOSet;
    }

    @Override
    public List<MenuDO> describeMenuListByPid(Long pid) {
        List<MenuDO> menuDOS;
        if (pid != null && !pid.equals(0L)) {
            menuDOS = menuMapper.queryMenuListByPidOrderByMenuSort(pid);
        } else {
            menuDOS = menuMapper.queryMenuListByPidIsNullOrderByMenuSort();
        }
        return menuDOS;
    }

    @Override
    public List<MenuDO> describeSuperiorMenuList(MenuDO menuDO, List<MenuDO> menuDOS) {
        if (menuDO.getPid() == null) {
            menuDOS.addAll(menuMapper.queryMenuListByPidIsNullOrderByMenuSort());
            return menuDOS;
        }
        menuDOS.addAll(menuMapper.queryMenuListByPidOrderByMenuSort(menuDO.getPid()));
        return describeSuperiorMenuList(describeMenuById(menuDO.getPid()), menuDOS);
    }

    @Override
    public List<MenuDO> buildMenuTree(List<MenuDO> menuDOS) {
        List<MenuDO> trees = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (MenuDO menuDO : menuDOS) {
            if (menuDO.getPid() == null) {
                trees.add(menuDO);
            }
            for (MenuDO it : menuDOS) {
                if (menuDO.getId().equals(it.getPid())) {
                    if (menuDO.getChildren() == null) {
                        menuDO.setChildren(new ArrayList<>());
                    }
                    menuDO.getChildren().add(it);
                    ids.add(it.getId());
                }
            }
        }
        if (CollUtil.isNotEmpty(trees)) {
            trees = menuDOS.stream().filter(s -> !ids.contains(s.getId())).collect(Collectors.toList());
        }
        return trees;
    }

    @Override
    public List<MenuVo> buildMenuResponse(List<MenuDO> menuDOS) {
        List<MenuVo> list = new LinkedList<>();
        menuDOS.forEach(menu -> {
                    if (menu != null) {
                        List<MenuDO> menuDOList = menu.getChildren();
                        MenuVo menuVo = new MenuVo();
                        menuVo.setName(ObjectUtil.isNotEmpty(menu.getComponentName()) ? menu.getComponentName() : menu.getTitle());
                        // 一级目录需要加斜杠，不然会报警告
                        menuVo.setPath(menu.getPid() == null ? "/" + menu.getPath() : menu.getPath());
                        menuVo.setHidden(menu.getHidden());
                        // 如果不是外链
                        if (!menu.getIFrame()) {
                            if (menu.getPid() == null) {
                                menuVo.setComponent(StringUtil.isEmpty(menu.getComponent()) ? "Layout" : menu.getComponent());
                                // 如果不是一级菜单，并且菜单类型为目录，则代表是多级菜单
                            } else if (menu.getType() == 0) {
                                menuVo.setComponent(StringUtil.isEmpty(menu.getComponent()) ? "ParentView" : menu.getComponent());
                            } else if (StringUtil.isNoneBlank(menu.getComponent())) {
                                menuVo.setComponent(menu.getComponent());
                            }
                        }
                        menuVo.setMeta(new MenuMetaVo(menu.getTitle(), menu.getIcon(), !menu.getCache()));
                        if (CollectionUtil.isNotEmpty(menuDOList)) {
                            menuVo.setAlwaysShow(true);
                            menuVo.setRedirect("noredirect");
                            menuVo.setChildren(buildMenuResponse(menuDOList));
                            // 处理是一级菜单并且没有子菜单的情况
                        } else if (menu.getPid() == null) {
                            MenuVo menuVo1 = getMenuVo(menu, menuVo);
                            menuVo.setName(null);
                            menuVo.setMeta(null);
                            menuVo.setComponent("Layout");
                            List<MenuVo> list1 = new ArrayList<>();
                            list1.add(menuVo1);
                            menuVo.setChildren(list1);
                        }
                        list.add(menuVo);
                    }
                }
        );
        return list;
    }

    /**
     * 获取 MenuVo
     *
     * @param menuDO /
     * @param menuVo /
     * @return /
     */
    private static MenuVo getMenuVo(MenuDO menuDO, MenuVo menuVo) {
        MenuVo menuVo1 = new MenuVo();
        menuVo1.setMeta(menuVo.getMeta());
        // 非外链
        if (!menuDO.getIFrame()) {
            menuVo1.setPath("index");
            menuVo1.setName(menuVo.getName());
            menuVo1.setComponent(menuVo.getComponent());
        } else {
            menuVo1.setPath(menuDO.getPath());
        }
        return menuVo1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(MenuDO resources) {
        if (menuMapper.getMenuByTitle(resources.getTitle()) != null) {
            throw new EntityExistException(MenuDO.class, "title", resources.getTitle());
        }
        if (StringUtil.isNotBlank(resources.getComponentName())) {
            if (menuMapper.getMenuByComponentName(resources.getComponentName()) != null) {
                throw new EntityExistException(MenuDO.class, "componentName", resources.getComponentName());
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
    public void modifyMenuById(MenuDO resources) {
        if (resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        MenuDO menuDO = getById(resources.getId());
        if (resources.getIFrame()) {
            if (!(resources.getPath().toLowerCase().startsWith(TransferProtocolConst.PREFIX_HTTP) || resources.getPath().toLowerCase().startsWith(TransferProtocolConst.PREFIX_HTTPS))) {
                throw new BadRequestException(TransferProtocolConst.PREFIX_HTTPS_BAD_REQUEST);
            }
        }
        MenuDO menuDO1 = menuMapper.getMenuByTitle(resources.getTitle());

        if (menuDO1 != null && !menuDO1.getId().equals(menuDO.getId())) {
            throw new EntityExistException(MenuDO.class, "title", resources.getTitle());
        }

        if (resources.getPid().equals(0L)) {
            resources.setPid(null);
        }

        // 记录的父节点ID
        Long oldPid = menuDO.getPid();
        Long newPid = resources.getPid();

        if (StringUtil.isNotBlank(resources.getComponentName())) {
            menuDO1 = menuMapper.getMenuByComponentName(resources.getComponentName());
            if (menuDO1 != null && !menuDO1.getId().equals(menuDO.getId())) {
                throw new EntityExistException(MenuDO.class, "componentName", resources.getComponentName());
            }
        }
        menuDO.setTitle(resources.getTitle());
        menuDO.setComponent(resources.getComponent());
        menuDO.setPath(resources.getPath());
        menuDO.setIcon(resources.getIcon());
        menuDO.setIFrame(resources.getIFrame());
        menuDO.setPid(resources.getPid());
        menuDO.setMenuSort(resources.getMenuSort());
        menuDO.setCache(resources.getCache());
        menuDO.setHidden(resources.getHidden());
        menuDO.setComponentName(resources.getComponentName());
        menuDO.setPermission(resources.getPermission());
        menuDO.setType(resources.getType());
        saveOrUpdate(menuDO);
        // 计算父级菜单节点数目
        updateSubCnt(oldPid);
        updateSubCnt(newPid);
        // 清理缓存
        delCaches(resources.getId());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMenuByIds(Set<MenuDO> menuDOSet) {
        for (MenuDO menuDO : menuDOSet) {
            // 清理缓存
            delCaches(menuDO.getId());
            roleMenuMapper.deleteByMenuId(menuDO.getId());
            menuMapper.deleteById(menuDO.getId());
            updateSubCnt(menuDO.getPid());
        }
    }


    @Override
    public void downloadMenuExcel(List<MenuDO> menuDOS, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MenuDO menuDO : menuDOS) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("菜单标题", menuDO.getTitle());
            map.put("菜单类型", menuDO.getType() == null ? "目录" : menuDO.getType() == 1 ? "菜单" : "按钮");
            map.put("权限标识", menuDO.getPermission());
            map.put("外链菜单", menuDO.getIFrame() ? YES_STR : NO_STR);
            map.put("菜单可见", menuDO.getHidden() ? NO_STR : YES_STR);
            map.put("是否缓存", menuDO.getCache() ? YES_STR : NO_STR);
            map.put("创建日期", menuDO.getCreateTime());
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
        List<UserDO> userDOS = userMapper.queryUserListByMenuId(id);
        redisHelper.del(RedisKeyConst.MENU_ID + id);
        redisHelper.delByKeys(RedisKeyConst.MENU_USER, userDOS.stream().map(UserDO::getId).collect(Collectors.toSet()));
        // 清除 RoleDO 缓存
        List<RoleDO> roleDOS = systemRoleService.describeRoleListByMenuId(id);
        redisHelper.delByKeys(RedisKeyConst.ROLE_ID, roleDOS.stream().map(RoleDO::getId).collect(Collectors.toSet()));
    }

}
