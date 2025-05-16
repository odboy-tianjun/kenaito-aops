package cn.odboy.core.controller.system;

import cn.odboy.core.service.system.SystemMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = "系统-服务监控管理")
@RequestMapping("/api/monitor")
public class MonitorController {

    private final SystemMonitorService serverService;

    @PostMapping(value = "/describeServerMonitorInfo")
    @ApiOperation("查询服务监控")
    @PreAuthorize("@el.check('monitor:list')")
    public ResponseEntity<Object> describeServerMonitorInfo() {
        return new ResponseEntity<>(serverService.describeServerMonitorInfo(), HttpStatus.OK);
    }
}
