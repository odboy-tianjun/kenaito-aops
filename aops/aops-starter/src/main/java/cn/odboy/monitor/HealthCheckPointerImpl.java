package cn.odboy.monitor;

import cn.odboy.core.service.system.SystemDictService;
import cn.odboy.monitor.service.HealthCheckPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class HealthCheckPointerImpl implements HealthCheckPointService {
    @Autowired
    private SystemDictService systemDictService;

    @Override
    public ResponseEntity<?> doReadiness() {
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<?> doLiveness() {
        return ResponseEntity.ok(systemDictService.getById(1));
    }
}
