package cn.odboy.core.controller.job;

import cn.odboy.base.PageResult;
import cn.odboy.context.SpringBeanHolder;
import cn.odboy.core.api.system.SystemQuartzJobApi;
import cn.odboy.core.service.system.dto.QueryQuartzJobRequest;
import cn.odboy.core.service.system.dto.UpdateQuartzJobRequest;
import cn.odboy.core.dal.dataobject.job.QuartzJob;
import cn.odboy.core.dal.dataobject.job.QuartzLog;
import cn.odboy.core.service.system.SystemQuartzJobService;
import cn.odboy.exception.BadRequestException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
@Api(tags = "系统:定时任务管理")
public class QuartzJobController {
    private static final String ENTITY_NAME = "quartzJob";
    private final SystemQuartzJobService systemQuartzJobService;
    private final SystemQuartzJobApi systemQuartzJobApi;

    @ApiOperation("查询定时任务")
    @GetMapping
    @PreAuthorize("@el.check('timing:list')")
    public ResponseEntity<PageResult<QuartzJob>> queryQuartzJob(QueryQuartzJobRequest criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(systemQuartzJobApi.describeQuartzJobPage(criteria, page), HttpStatus.OK);
    }

    @ApiOperation("导出任务数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('timing:list')")
    public void exportQuartzJob(HttpServletResponse response, QueryQuartzJobRequest criteria) throws IOException {
        systemQuartzJobService.downloadQuartzJobExcel(systemQuartzJobApi.describeQuartzJobList(criteria), response);
    }

    @ApiOperation("导出日志数据")
    @GetMapping(value = "/logs/download")
    @PreAuthorize("@el.check('timing:list')")
    public void exportQuartzJobLog(HttpServletResponse response, QueryQuartzJobRequest criteria) throws IOException {
        systemQuartzJobService.downloadQuartzLogExcel(systemQuartzJobApi.describeQuartzLogList(criteria), response);
    }

    @ApiOperation("查询任务执行日志")
    @GetMapping(value = "/logs")
    @PreAuthorize("@el.check('timing:list')")
    public ResponseEntity<PageResult<QuartzLog>> queryQuartzJobLog(QueryQuartzJobRequest criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(systemQuartzJobApi.describeQuartzLogPage(criteria, page), HttpStatus.OK);
    }

    @ApiOperation("新增定时任务")
    @PostMapping
    @PreAuthorize("@el.check('timing:add')")
    public ResponseEntity<Object> createQuartzJob(@Validated @RequestBody QuartzJob resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        // 验证Bean是不是合法的，合法的定时任务 Bean 需要用 @Service 定义
        checkBean(resources.getBeanName());
        systemQuartzJobService.createJob(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("修改定时任务")
    @PutMapping
    @PreAuthorize("@el.check('timing:edit')")
    public ResponseEntity<Object> updateQuartzJob(@Validated(QuartzJob.Update.class) @RequestBody UpdateQuartzJobRequest resources) {
        // 验证Bean是不是合法的，合法的定时任务 Bean 需要用 @Service 定义
        checkBean(resources.getBeanName());
        systemQuartzJobService.modifyQuartzJobResumeCron(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("更改定时任务状态")
    @PostMapping(value = "/switchQuartzJobStatus/{id}")
    @PreAuthorize("@el.check('timing:edit')")
    public ResponseEntity<Object> switchQuartzJobStatus(@PathVariable Long id) {
        systemQuartzJobService.switchQuartzJobStatus(systemQuartzJobService.getById(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("执行定时任务")
    @PostMapping(value = "/startQuartzJob/{id}")
    @PreAuthorize("@el.check('timing:edit')")
    public ResponseEntity<Object> startQuartzJob(@PathVariable Long id) {
        systemQuartzJobService.startQuartzJob(systemQuartzJobService.getById(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("删除定时任务")
    @DeleteMapping
    @PreAuthorize("@el.check('timing:del')")
    public ResponseEntity<Object> removeJobByIds(@RequestBody Set<Long> ids) {
        systemQuartzJobService.removeJobByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 验证Bean是不是合法的，合法的定时任务 Bean 需要用 @Service 定义
     *
     * @param beanName Bean名称
     */
    private void checkBean(String beanName) {
        // 避免调用攻击者可以从SpringContextHolder获得控制jdbcTemplate类
        // 并使用getDeclaredMethod调用jdbcTemplate的queryForMap函数，执行任意sql命令。
        if (!SpringBeanHolder.getAllServiceBeanName().contains(beanName)) {
            throw new BadRequestException("非法的 Bean，请重新输入！");
        }
    }
}
