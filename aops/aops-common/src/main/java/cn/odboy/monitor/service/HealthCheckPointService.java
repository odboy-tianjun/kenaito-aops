package cn.odboy.monitor.service;

import org.springframework.http.ResponseEntity;

public interface HealthCheckPointService {
    /**
     * 就绪检查
     */
    ResponseEntity<?> doReadiness();

    /**
     * 存活检查
     */
    ResponseEntity<?> doLiveness();
}
