package cn.odboy.core.framework.flow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 流水线任务池
 *
 * @author odboy
 * @date 2025-05-09
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pipeline-task-pool")
public class PipelineTaskPoolProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Long keepAliveTime;
    private Integer queueCapacity;
}
