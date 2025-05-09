package cn.odboy.app.framework.kubernetes.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Kubernetes配置
 *
 * @author odboy
 * @date 2025-05-08
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kubernetes")
public class KubernetesProperties {
    /**
     * 模版文件版本
     */
    private String templateVersion;
}
