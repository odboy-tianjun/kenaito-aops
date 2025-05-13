package cn.odboy.core.controller.system;

import cn.hutool.core.collection.CollectionUtil;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.controller.system.vo.MenuVo;
import cn.odboy.core.dal.dataobject.system.MenuDO;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import cn.odboy.core.framework.permission.core.util.SecurityHelper;
import cn.odboy.core.service.system.dto.QueryMenuArgs;
import cn.odboy.core.service.system.SystemMenuService;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.util.PageUtil;
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
    private final SystemMenuService systemMenuService;
    @OperationLog
    @ApiOperation("导出菜单数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('menu:list')")
    public void exportMenu(HttpServletResponse response, QueryMenuArgs args) throws Exception {
        systemMenuService.downloadMenuExcel(systemMenuService.describeMenuList(args, false), response);
    }

    @PostMapping(value = "/buildMenus")
    @ApiOperation("获取前端所需菜单")
    public ResponseEntity<List<MenuVo>> buildMenus() {
        List<MenuDO> menuDOList = systemMenuService.describeMenuListByUserId(SecurityHelper.getCurrentUserId());
        List<MenuDO> menuDOS = systemMenuService.buildMenuTree(menuDOList);
        return new ResponseEntity<>(systemMenuService.buildMenuResponse(menuDOS), HttpStatus.OK);
    }

    @ApiOperation("返回全部的菜单")
    @PostMapping(value = "/describeMenuListByPid")
    @PreAuthorize("@el.check('menu:list','roles:list')")
    public ResponseEntity<List<MenuDO>> describeMenuListByPid(@RequestParam Long pid) {
        return new ResponseEntity<>(systemMenuService.describeMenuListByPid(pid), HttpStatus.OK);
    }

    @ApiOperation("根据菜单ID返回所有子节点ID，包含自身ID")
    @PostMapping(value = "/describeChildMenuSet")
    @PreAuthorize("@el.check('menu:list','roles:list')")
    public ResponseEntity<Object> describeChildMenuSet(@RequestParam Long id) {
        Set<MenuDO> menuDOSet = new HashSet<>();
        List<MenuDO> menuDOList = systemMenuService.describeMenuListByPid(id);
        menuDOSet.add(systemMenuService.describeMenuById(id));
        menuDOSet = systemMenuService.describeChildMenuSet(menuDOList, menuDOSet);
        Set<Long> ids = menuDOSet.stream().map(MenuDO::getId).collect(Collectors.toSet());
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation("查询菜单")
    @PreAuthorize("@el.check('menu:list')")
    public ResponseEntity<PageResult<MenuDO>> queryMenu(QueryMenuArgs args) throws Exception {
        List<MenuDO> menuDOList = systemMenuService.describeMenuList(args, true);
        return new ResponseEntity<>(PageUtil.toPage(menuDOList), HttpStatus.OK);
    }

    @ApiOperation("查询菜单:根据ID获取同级与上级数据")
    @PostMapping("/describeMenuSuperior")
    @PreAuthorize("@el.check('menu:list')")
    public ResponseEntity<List<MenuDO>> describeMenuSuperior(@RequestBody List<Long> ids) {
        Set<MenuDO> menuDOS = new LinkedHashSet<>();
        if (CollectionUtil.isNotEmpty(ids)) {
            for (Long id : ids) {
                MenuDO menuDO = systemMenuService.describeMenuById(id);
                List<MenuDO> menuDOList = systemMenuService.describeSuperiorMenuList(menuDO, new ArrayList<>());
                for (MenuDO data : menuDOList) {
                    if (data.getId().equals(menuDO.getPid())) {
                        data.setSubCount(data.getSubCount() - 1);
                    }
                }
                menuDOS.addAll(menuDOList);
            }
            // 编辑菜单时不显示自己以及自己下级的数据，避免出现PID数据环形问题
            menuDOS = menuDOS.stream().filter(i -> !ids.contains(i.getId())).collect(Collectors.toSet());
            return new ResponseEntity<>(systemMenuService.buildMenuTree(new ArrayList<>(menuDOS)), HttpStatus.OK);
        }
        return new ResponseEntity<>(systemMenuService.describeMenuListByPid(null), HttpStatus.OK);
    }
    @OperationLog
    @ApiOperation("新增菜单")
    @PostMapping(value = "/saveMenu")
    @PreAuthorize("@el.check('menu:add')")
    public ResponseEntity<Object> saveMenu(@Validated @RequestBody MenuDO resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        systemMenuService.saveMenu(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @OperationLog
    @ApiOperation("修改菜单")
    @PostMapping(value = "/modifyMenuById")
    @PreAuthorize("@el.check('menu:edit')")
    public ResponseEntity<Object> modifyMenuById(@Validated(MenuDO.Update.class) @RequestBody MenuDO resources) {
        systemMenuService.modifyMenuById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @OperationLog
    @ApiOperation("删除菜单")
    @PostMapping(value = "/removeMenuByIds")
    @PreAuthorize("@el.check('menu:del')")
    public ResponseEntity<Object> removeMenuByIds(@RequestBody Set<Long> ids) {
        Set<MenuDO> menuDOSet = new HashSet<>();
        for (Long id : ids) {
            List<MenuDO> menuDOList = systemMenuService.describeMenuListByPid(id);
            menuDOSet.add(systemMenuService.describeMenuById(id));
            menuDOSet = systemMenuService.describeChildMenuSet(menuDOList, menuDOSet);
        }
        systemMenuService.removeMenuByIds(menuDOSet);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
