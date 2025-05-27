package cn.odboy.core.controller.system;

import cn.odboy.common.model.PageResult;
import cn.odboy.common.util.PageUtil;
import cn.odboy.core.dal.dataobject.system.DeptDO;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import cn.odboy.core.service.system.SystemDeptService;
import cn.odboy.core.service.system.dto.DeptCreateArgs;
import cn.odboy.core.service.system.dto.DeptQueryArgs;
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
@Api(tags = "系统：部门管理")
@RequestMapping("/api/dept")
public class DeptController {
    private final SystemDeptService systemDeptService;

    @OperationLog
    @ApiOperation("导出部门数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dept:list')")
    public void exportDept(HttpServletResponse response, DeptQueryArgs args) throws Exception {
        systemDeptService.downloadDeptExcel(systemDeptService.describeDeptList(args, false), response);
    }

    @ApiOperation("查询部门")
    @GetMapping
    @PreAuthorize("@el.check('user:list','dept:list')")
    public ResponseEntity<PageResult<DeptDO>> describeDeptPage(DeptQueryArgs args) throws Exception {
        List<DeptDO> deptDOS = systemDeptService.describeDeptList(args, true);
        return new ResponseEntity<>(PageUtil.toPage(deptDOS), HttpStatus.OK);
    }

    @ApiOperation("查询部门:根据ID获取同级与上级数据")
    @PostMapping("/describeDeptSuperiorTree")
    @PreAuthorize("@el.check('user:list','dept:list')")
    public ResponseEntity<Object> describeDeptSuperiorTree(@RequestBody List<Long> ids, @RequestParam(defaultValue = "false") Boolean exclude) {
        Set<DeptDO> deptDOSet = new LinkedHashSet<>();
        for (Long id : ids) {
            DeptDO deptDO = systemDeptService.describeDeptById(id);
            List<DeptDO> deptDOS = systemDeptService.describeSuperiorDeptListByPid(deptDO, new ArrayList<>());
            if (exclude) {
                for (DeptDO data : deptDOS) {
                    if (data.getId().equals(deptDO.getPid())) {
                        data.setSubCount(data.getSubCount() - 1);
                    }
                }
                // 编辑部门时不显示自己以及自己下级的数据，避免出现PID数据环形问题
                deptDOS = deptDOS.stream().filter(i -> !ids.contains(i.getId())).collect(Collectors.toList());
            }
            deptDOSet.addAll(deptDOS);
        }
        return new ResponseEntity<>(systemDeptService.buildDeptTree(new ArrayList<>(deptDOSet)), HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("新增部门")
    @PostMapping(value = "/saveDept")
    @PreAuthorize("@el.check('dept:add')")
    public ResponseEntity<Object> saveDept(@Validated @RequestBody DeptCreateArgs args) {
        systemDeptService.saveDept(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @OperationLog
    @ApiOperation("修改部门")
    @PostMapping(value = "/modifyDept")
    @PreAuthorize("@el.check('dept:edit')")
    public ResponseEntity<Object> modifyDept(@Validated(DeptDO.Update.class) @RequestBody DeptDO resources) {
        systemDeptService.modifyDept(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @OperationLog
    @ApiOperation("删除部门")
    @PostMapping(value = "/removeDeptByIds")
    @PreAuthorize("@el.check('dept:del')")
    public ResponseEntity<Object> removeDeptByIds(@RequestBody Set<Long> ids) {
        Set<DeptDO> deptDOS = new HashSet<>();
        // 获取部门，和其所有子部门
        systemDeptService.traverseDeptByIdWithPids(ids, deptDOS);
        // 验证是否被角色或用户关联
        systemDeptService.verifyBindRelationByIds(deptDOS);
        systemDeptService.removeDeptByIds(deptDOS);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
