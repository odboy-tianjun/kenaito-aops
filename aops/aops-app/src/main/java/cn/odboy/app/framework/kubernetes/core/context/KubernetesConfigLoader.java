/*
 *  Copyright 2021-2025 Odboy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.app.framework.kubernetes.core.context;

import cn.odboy.app.dal.dataobject.AopsKubernetesClusterConfigDO;
import cn.odboy.app.framework.kubernetes.core.listener.KubernetesResourceListener;
import cn.odboy.app.service.kubernetes.AopsKubernetesClusterConfigService;
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

//    private void updateStatusById(AopsKubernetesClusterConfigDO aopsKubernetesClusterConfigDO, KubernetesResourceHealthStatusEnum kubernetesResourceHealthStatusEnum) {
//        try {
//            aopsKubernetesClusterConfigService.modifyMetaById(aopsKubernetesClusterConfigDO.getId(), kubernetesResourceHealthStatusEnum);
//        } catch (Exception e) {
//            log.error("根据id修改集群健康状态失败", e);
//        }
//    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<AopsKubernetesClusterConfigDO> list = aopsKubernetesClusterConfigService.describeKubernetesClusterConfigList();
        for (AopsKubernetesClusterConfigDO aopsKubernetesClusterConfigDO : list) {
            try {
                ApiClient apiClient = kubernetesConfigHelper.loadFormContent(aopsKubernetesClusterConfigDO.getClusterConfigContent());
                kubernetesApiClientManager.putClientEnv(aopsKubernetesClusterConfigDO.getClusterCode(), aopsKubernetesClusterConfigDO.getEnvCode(), apiClient);
//                updateStatusById(aopsKubernetesClusterConfigDO, KubernetesResourceHealthStatusEnum.HEALTH);
                log.info("Kubernetes集群 {} 服务端健康，已加入k8sClientAdmin", aopsKubernetesClusterConfigDO.getClusterName());
                kubernetesResourceListener.addListener(aopsKubernetesClusterConfigDO.getClusterCode(), aopsKubernetesClusterConfigDO.getClusterConfigContent());
                log.info("Kubernetes集群 {} 服务端健康，已开启资源变更监听", aopsKubernetesClusterConfigDO.getClusterName());
            } catch (Exception e) {
                log.error("Kubernetes集群 {} 服务端不健康", aopsKubernetesClusterConfigDO.getClusterName(), e);
//                updateStatusById(aopsKubernetesClusterConfigDO, KubernetesResourceHealthStatusEnum.UN_HEALTH);
            }
        }
    }
}
