package cn.odboy.core.controller.system;

import cn.odboy.common.model.PageResult;
import cn.odboy.core.dal.dataobject.system.JobDO;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import cn.odboy.core.service.system.SystemJobService;
import cn.odboy.core.service.system.dto.JobCreateArgs;
import cn.odboy.core.service.system.dto.JobQueryArgs;
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
import java.util.Set;


@RestController
@RequiredArgsConstructor
@Api(tags = "系统：岗位管理")
@RequestMapping("/api/job")
public class JobController {
    private final SystemJobService systemJobService;

    @OperationLog
    @ApiOperation("导出岗位数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('job:list')")
    public void exportJob(HttpServletResponse response, JobQueryArgs args) throws IOException {
        systemJobService.downloadJobExcel(systemJobService.describeJobList(args), response);
    }

    @ApiOperation("查询岗位")
    @GetMapping
    @PreAuthorize("@el.check('job:list','user:list')")
    public ResponseEntity<PageResult<JobDO>> queryJob(JobQueryArgs args) {
        Page<Object> page = new Page<>(args.getPage(), args.getSize());
        return new ResponseEntity<>(systemJobService.describeJobPage(args, page), HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("新增岗位")
    @PostMapping(value = "/saveJob")
    @PreAuthorize("@el.check('job:add')")
    public ResponseEntity<Object> saveJob(@Validated @RequestBody JobCreateArgs args) {
        systemJobService.saveJob(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @OperationLog
    @ApiOperation("修改岗位")
    @PostMapping(value = "/modifyJobById")
    @PreAuthorize("@el.check('job:edit')")
    public ResponseEntity<Object> modifyJobById(@Validated(JobDO.Update.class) @RequestBody JobDO resources) {
        systemJobService.modifyJobById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @OperationLog
    @ApiOperation("删除岗位")
    @PostMapping(value = "/removeJobByIds")
    @PreAuthorize("@el.check('job:del')")
    public ResponseEntity<Object> removeJobByIds(@RequestBody Set<Long> ids) {
        // 验证是否被用户关联
        systemJobService.verifyBindRelationByIds(ids);
        systemJobService.removeJobByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
