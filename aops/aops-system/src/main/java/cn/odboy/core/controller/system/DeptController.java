package cn.odboy.core.controller.system;

import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemDeptApi;
import cn.odboy.core.service.system.dto.CreateDeptRequest;
import cn.odboy.core.service.system.dto.QueryDeptRequest;
import cn.odboy.core.dal.dataobject.system.Dept;
import cn.odboy.core.service.system.SystemDeptService;
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
@Api(tags = "系统：部门管理")
@RequestMapping("/api/dept")
public class DeptController {
    private final SystemDeptApi systemDeptApi;
    private final SystemDeptService systemDeptService;

    @ApiOperation("导出部门数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dept:list')")
    public void exportDept(HttpServletResponse response, QueryDeptRequest criteria) throws Exception {
        systemDeptService.downloadDeptExcel(systemDeptApi.describeDeptList(criteria, false), response);
    }

    @ApiOperation("查询部门")
    @GetMapping
    @PreAuthorize("@el.check('user:list','dept:list')")
    public ResponseEntity<PageResult<Dept>> describeDeptPage(QueryDeptRequest criteria) throws Exception {
        List<Dept> depts = systemDeptApi.describeDeptList(criteria, true);
        return new ResponseEntity<>(PageUtil.toPage(depts), HttpStatus.OK);
    }

    @ApiOperation("查询部门:根据ID获取同级与上级数据")
    @PostMapping("/describeDeptSuperiorTree")
    @PreAuthorize("@el.check('user:list','dept:list')")
    public ResponseEntity<Object> describeDeptSuperiorTree(@RequestBody List<Long> ids, @RequestParam(defaultValue = "false") Boolean exclude) {
        Set<Dept> deptSet = new LinkedHashSet<>();
        for (Long id : ids) {
            Dept dept = systemDeptApi.describeDeptById(id);
            List<Dept> depts = systemDeptApi.describeSuperiorDeptListByPid(dept, new ArrayList<>());
            if (exclude) {
                for (Dept data : depts) {
                    if (data.getId().equals(dept.getPid())) {
                        data.setSubCount(data.getSubCount() - 1);
                    }
                }
                // 编辑部门时不显示自己以及自己下级的数据，避免出现PID数据环形问题
                depts = depts.stream().filter(i -> !ids.contains(i.getId())).collect(Collectors.toList());
            }
            deptSet.addAll(depts);
        }
        return new ResponseEntity<>(systemDeptApi.buildDeptTree(new ArrayList<>(deptSet)), HttpStatus.OK);
    }

    @ApiOperation("新增部门")
    @PostMapping(value = "/saveDept")
    @PreAuthorize("@el.check('dept:add')")
    public ResponseEntity<Object> saveDept(@Validated @RequestBody CreateDeptRequest resources) {
        systemDeptService.saveDept(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("修改部门")
    @PostMapping(value = "/modifyDept")
    @PreAuthorize("@el.check('dept:edit')")
    public ResponseEntity<Object> modifyDept(@Validated(Dept.Update.class) @RequestBody Dept resources) {
        systemDeptService.modifyDept(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("删除部门")
    @PostMapping(value = "/removeDeptByIds")
    @PreAuthorize("@el.check('dept:del')")
    public ResponseEntity<Object> removeDeptByIds(@RequestBody Set<Long> ids) {
        Set<Dept> depts = new HashSet<>();
        // 获取部门，和其所有子部门
        systemDeptApi.traverseDeptByIdWithPids(ids, depts);
        // 验证是否被角色或用户关联
        systemDeptApi.verifyBindRelationByIds(depts);
        systemDeptService.removeDeptByIds(depts);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}