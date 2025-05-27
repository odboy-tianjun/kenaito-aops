package cn.odboy.core.controller.system;

import cn.hutool.core.lang.Dict;
import cn.odboy.common.model.PageResult;
import cn.odboy.core.dal.dataobject.system.RoleDO;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import cn.odboy.core.framework.permission.core.util.SecurityHelper;
import cn.odboy.core.service.system.SystemRoleService;
import cn.odboy.core.service.system.dto.RoleCreateArgs;
import cn.odboy.core.service.system.dto.RoleQueryArgs;
import cn.odboy.common.exception.BadRequestException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Api(tags = "系统：角色管理")
@RequestMapping("/api/roles")
public class RoleController {
    private final SystemRoleService systemRoleService;

    @ApiOperation("查询单个role")
    @PostMapping(value = "/describeRoleById")
    @PreAuthorize("@el.check('roles:list')")
    public ResponseEntity<RoleDO> describeRoleById(@RequestBody RoleDO args) {
        return new ResponseEntity<>(systemRoleService.describeRoleById(args.getId()), HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("导出角色数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('role:list')")
    public void exportRole(HttpServletResponse response, RoleQueryArgs args) throws IOException {
        systemRoleService.downloadRoleExcel(systemRoleService.describeRoleList(args), response);
    }

    @ApiOperation("返回全部的角色")
    @PostMapping(value = "/describeRoleList")
    @PreAuthorize("@el.check('roles:list','user:add','user:edit')")
    public ResponseEntity<List<RoleDO>> describeRoleList() {
        return new ResponseEntity<>(systemRoleService.describeRoleList(), HttpStatus.OK);
    }

    @ApiOperation("查询角色")
    @GetMapping
    @PreAuthorize("@el.check('roles:list')")
    public ResponseEntity<PageResult<RoleDO>> describeRolePage(RoleQueryArgs args) {
        Page<Object> page = new Page<>(args.getPage(), args.getSize());
        return new ResponseEntity<>(systemRoleService.describeRolePage(args, page), HttpStatus.OK);
    }

    @ApiOperation("查询用户级别")
    @PostMapping(value = "/describeRoleLevel")
    public ResponseEntity<Object> describeRoleLevel() {
        return new ResponseEntity<>(Dict.create().set("level", checkRoleLevels(null)), HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("新增角色")
    @PostMapping(value = "/saveRole")
    @PreAuthorize("@el.check('roles:add')")
    public ResponseEntity<Object> saveRole(@Validated @RequestBody RoleCreateArgs args) {
        checkRoleLevels(args.getLevel());
        systemRoleService.saveRole(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @OperationLog
    @ApiOperation("修改角色")
    @PostMapping(value = "/modifyRoleById")
    @PreAuthorize("@el.check('roles:edit')")
    public ResponseEntity<Object> modifyRoleById(@Validated(RoleDO.Update.class) @RequestBody RoleDO resources) {
        checkRoleLevels(resources.getLevel());
        systemRoleService.modifyRoleById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @OperationLog
    @ApiOperation("修改角色菜单")
    @PostMapping(value = "/modifyBindMenuById")
    @PreAuthorize("@el.check('roles:edit')")
    public ResponseEntity<Object> modifyBindMenuById(@RequestBody RoleDO resources) {
        RoleDO roleDO = systemRoleService.getById(resources.getId());
        checkRoleLevels(roleDO.getLevel());
        systemRoleService.modifyBindMenuById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @OperationLog
    @ApiOperation("删除角色")
    @PostMapping(value = "/removeRoleByIds")
    @PreAuthorize("@el.check('roles:del')")
    public ResponseEntity<Object> removeRoleByIds(@RequestBody Set<Long> ids) {
        for (Long id : ids) {
            RoleDO roleDO = systemRoleService.getById(id);
            checkRoleLevels(roleDO.getLevel());
        }
        // 验证是否被用户关联
        systemRoleService.verifyBindRelationByIds(ids);
        systemRoleService.removeRoleByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 检查用户的角色级别
     *
     * @return /
     */
    private int checkRoleLevels(Integer level) {
        List<Integer> levels = systemRoleService.describeRoleListByUsersId(SecurityHelper.getCurrentUserId()).stream().map(RoleDO::getLevel).collect(Collectors.toList());
        int min = Collections.min(levels);
        if (level != null) {
            if (level < min) {
                throw new BadRequestException("权限不足，你的角色级别：" + min + "，低于操作的角色级别：" + level);
            }
        }
        return min;
    }
}
