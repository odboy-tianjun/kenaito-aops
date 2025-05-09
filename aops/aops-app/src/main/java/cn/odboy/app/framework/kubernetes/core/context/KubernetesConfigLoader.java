package cn.odboy.app.framework.kubernetes.core.context;

import cn.odboy.app.dal.dataobject.AopsKubernetesClusterConfigDO;
import cn.odboy.app.service.kubernetes.AopsKubernetesClusterConfigService;
import cn.odboy.app.framework.kubernetes.core.constant.KubernetesResourceHealthStatusEnum;
import cn.odboy.app.framework.kubernetes.core.listener.KubernetesResourceListener;
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

    private void updateStatusById(AopsKubernetesClusterConfigDO aopsKubernetesClusterConfigDO, KubernetesResourceHealthStatusEnum kubernetesResourceHealthStatusEnum) {
        try {
            aopsKubernetesClusterConfigService.modifyStatusById(aopsKubernetesClusterConfigDO.getId(), kubernetesResourceHealthStatusEnum);
        } catch (Exception e) {
            log.error("根据id修改集群健康状态失败", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<AopsKubernetesClusterConfigDO> list = aopsKubernetesClusterConfigService.describeKubernetesClusterConfig();
        for (AopsKubernetesClusterConfigDO aopsKubernetesClusterConfigDO : list) {
            try {
                ApiClient apiClient = kubernetesConfigHelper.loadFormContent(aopsKubernetesClusterConfigDO.getConfigContent());
                kubernetesApiClientManager.putClientEnv(aopsKubernetesClusterConfigDO.getClusterCode(), aopsKubernetesClusterConfigDO.getEnvCode(), apiClient);
                updateStatusById(aopsKubernetesClusterConfigDO, KubernetesResourceHealthStatusEnum.HEALTH);
                log.info("K8s集群 {} 服务端健康，已加入k8sClientAdmin", aopsKubernetesClusterConfigDO.getClusterName());
                kubernetesResourceListener.addListener(aopsKubernetesClusterConfigDO.getClusterCode(), aopsKubernetesClusterConfigDO.getConfigContent());
                log.info("K8s集群 {} 服务端健康，已开启资源变更监听", aopsKubernetesClusterConfigDO.getClusterName());
            } catch (Exception e) {
                log.error("K8s集群 {} 服务端不健康", aopsKubernetesClusterConfigDO.getClusterName(), e);
                updateStatusById(aopsKubernetesClusterConfigDO, KubernetesResourceHealthStatusEnum.UN_HEALTH);
            }
        }
    }
}
