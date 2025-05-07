package cn.odboy.framework.kubernetes.context;

import cn.odboy.dal.dataobject.AopsKubernetesClusterConfig;
import cn.odboy.framework.kubernetes.constant.KubernetesResourceHealthStatusEnum;
import cn.odboy.framework.kubernetes.listener.KubernetesResourceListener;
import cn.odboy.service.AopsKubernetesClusterConfigService;
import io.kubernetes.client.openapi.ApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Kubernetes 配置加载器
 *
 * @author odboy
 * @date 2025-01-13
 */
@Slf4j
@Component
public class KubernetesConfigLoader implements InitializingBean {
    @Autowired
    private AopsKubernetesClusterConfigService aopsKubernetesClusterConfigService;
    @Autowired
    private KubernetesApiClientManager kubernetesApiClientManager;
    @Autowired
    private KubernetesConfigHelper kubernetesConfigHelper;
    @Autowired
    private KubernetesResourceListener kubernetesResourceListener;

    private void updateStatusById(AopsKubernetesClusterConfig aopsKubernetesClusterConfig, KubernetesResourceHealthStatusEnum kubernetesResourceHealthStatusEnum) {
        try {
            aopsKubernetesClusterConfigService.modifyStatusById(aopsKubernetesClusterConfig.getId(), kubernetesResourceHealthStatusEnum);
        } catch (Exception e) {
            log.error("根据id修改集群健康状态失败", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<AopsKubernetesClusterConfig> list = aopsKubernetesClusterConfigService.describeKubernetesClusterConfig();
        for (AopsKubernetesClusterConfig aopsKubernetesClusterConfig : list) {
            try {
                ApiClient apiClient = kubernetesConfigHelper.loadFormContent(aopsKubernetesClusterConfig.getConfigContent());
                kubernetesApiClientManager.putClientEnv(aopsKubernetesClusterConfig.getEnvCode(), aopsKubernetesClusterConfig.getEnvCode(), apiClient);
                updateStatusById(aopsKubernetesClusterConfig, KubernetesResourceHealthStatusEnum.HEALTH);
                log.info("K8s集群 {} 服务端健康，已加入k8sClientAdmin", aopsKubernetesClusterConfig.getClusterName());
                kubernetesResourceListener.addListener(aopsKubernetesClusterConfig.getClusterCode(), aopsKubernetesClusterConfig.getConfigContent());
                log.info("K8s集群 {} 服务端健康，已开启资源变更监听", aopsKubernetesClusterConfig.getClusterName());
            } catch (Exception e) {
                log.error("K8s集群 {} 服务端不健康", aopsKubernetesClusterConfig.getClusterName(), e);
                updateStatusById(aopsKubernetesClusterConfig, KubernetesResourceHealthStatusEnum.UN_HEALTH);
            }
        }
    }
}
