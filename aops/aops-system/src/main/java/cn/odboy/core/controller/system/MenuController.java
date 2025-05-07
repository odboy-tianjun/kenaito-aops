package cn.odboy.core.controller.system;

import cn.hutool.core.collection.CollectionUtil;
import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemMenuApi;
import cn.odboy.core.framework.permission.util.SecurityHelper;
import cn.odboy.core.service.system.dto.QueryMenuRequest;
import cn.odboy.core.dal.dataobject.system.Menu;
import cn.odboy.core.dal.model.MenuResponse;
import cn.odboy.core.service.system.SystemMenuService;
import cn.odboy.exception.BadRequestException;
import cn.odboy.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Api(tags = "系统：菜单管理")
@RequestMapping("/api/menus")
public class MenuController {
    private static final String ENTITY_NAME = "menu";
    private final SystemMenuApi systemMenuApi;
    private final SystemMenuService systemMenuService;

    @ApiOperation("导出菜单数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('menu:list')")
    public void exportMenu(HttpServletResponse response, QueryMenuRequest criteria) throws Exception {
        systemMenuService.downloadMenuExcel(systemMenuApi.describeMenuList(criteria, false), response);
    }

    @PostMapping(value = "/buildMenus")
    @ApiOperation("获取前端所需菜单")
    public ResponseEntity<List<MenuResponse>> buildMenus() {
        List<Menu> menuList = systemMenuApi.describeMenuListByUserId(SecurityHelper.getCurrentUserId());
        List<Menu> menus = systemMenuApi.buildMenuTree(menuList);
        return new ResponseEntity<>(systemMenuApi.buildMenuResponse(menus), HttpStatus.OK);
    }

    @ApiOperation("返回全部的菜单")
    @PostMapping(value = "/describeMenuListByPid")
    @PreAuthorize("@el.check('menu:list','roles:list')")
    public ResponseEntity<List<Menu>> describeMenuListByPid(@RequestParam Long pid) {
        return new ResponseEntity<>(systemMenuApi.describeMenuListByPid(pid), HttpStatus.OK);
    }

    @ApiOperation("根据菜单ID返回所有子节点ID，包含自身ID")
    @PostMapping(value = "/describeChildMenuSet")
    @PreAuthorize("@el.check('menu:list','roles:list')")
    public ResponseEntity<Object> describeChildMenuSet(@RequestParam Long id) {
        Set<Menu> menuSet = new HashSet<>();
        List<Menu> menuList = systemMenuApi.describeMenuListByPid(id);
        menuSet.add(systemMenuApi.describeMenuById(id));
        menuSet = systemMenuApi.describeChildMenuSet(menuList, menuSet);
        Set<Long> ids = menuSet.stream().map(Menu::getId).collect(Collectors.toSet());
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation("查询菜单")
    @PreAuthorize("@el.check('menu:list')")
    public ResponseEntity<PageResult<Menu>> queryMenu(QueryMenuRequest criteria) throws Exception {
        List<Menu> menuList = systemMenuApi.describeMenuList(criteria, true);
        return new ResponseEntity<>(PageUtil.toPage(menuList), HttpStatus.OK);
    }

    @ApiOperation("查询菜单:根据ID获取同级与上级数据")
    @PostMapping("/describeMenuSuperior")
    @PreAuthorize("@el.check('menu:list')")
    public ResponseEntity<List<Menu>> describeMenuSuperior(@RequestBody List<Long> ids) {
        Set<Menu> menus = new LinkedHashSet<>();
        if (CollectionUtil.isNotEmpty(ids)) {
            for (Long id : ids) {
                Menu menu = systemMenuApi.describeMenuById(id);
                List<Menu> menuList = systemMenuApi.describeSuperiorMenuList(menu, new ArrayList<>());
                for (Menu data : menuList) {
                    if (data.getId().equals(menu.getPid())) {
                        data.setSubCount(data.getSubCount() - 1);
                    }
                }
                menus.addAll(menuList);
            }
            // 编辑菜单时不显示自己以及自己下级的数据，避免出现PID数据环形问题
            menus = menus.stream().filter(i -> !ids.contains(i.getId())).collect(Collectors.toSet());
            return new ResponseEntity<>(systemMenuApi.buildMenuTree(new ArrayList<>(menus)), HttpStatus.OK);
        }
        return new ResponseEntity<>(systemMenuApi.describeMenuListByPid(null), HttpStatus.OK);
    }

    @ApiOperation("新增菜单")
    @PostMapping(value = "/saveMenu")
    @PreAuthorize("@el.check('menu:add')")
    public ResponseEntity<Object> saveMenu(@Validated @RequestBody Menu resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        systemMenuService.saveMenu(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("修改菜单")
    @PostMapping(value = "/modifyMenuById")
    @PreAuthorize("@el.check('menu:edit')")
    public ResponseEntity<Object> modifyMenuById(@Validated(Menu.Update.class) @RequestBody Menu resources) {
        systemMenuService.modifyMenuById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("删除菜单")
    @PostMapping(value = "/removeMenuByIds")
    @PreAuthorize("@el.check('menu:del')")
    public ResponseEntity<Object> removeMenuByIds(@RequestBody Set<Long> ids) {
        Set<Menu> menuSet = new HashSet<>();
        for (Long id : ids) {
            List<Menu> menuList = systemMenuApi.describeMenuListByPid(id);
            menuSet.add(systemMenuApi.describeMenuById(id));
            menuSet = systemMenuApi.describeChildMenuSet(menuList, menuSet);
        }
        systemMenuService.removeMenuByIds(menuSet);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
