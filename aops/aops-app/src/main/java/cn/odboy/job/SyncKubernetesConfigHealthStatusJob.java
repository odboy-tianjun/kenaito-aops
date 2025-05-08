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
package cn.odboy.job;

import cn.odboy.dal.dataobject.AopsKubernetesClusterConfig;
import cn.odboy.framework.kubernetes.constant.KubernetesResourceHealthStatusEnum;
import cn.odboy.framework.kubernetes.context.KubernetesConfigHelper;
import cn.odboy.framework.kubernetes.context.KubernetesHealthChecker;
import cn.odboy.service.AopsKubernetesClusterConfigService;
import cn.odboy.util.CollUtil;
import io.kubernetes.client.openapi.ApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 同步K8s集群配置健康状态
 *
 * @author odboy
 * @date 2024-11-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncKubernetesConfigHealthStatusJob {
    private final KubernetesHealthChecker kubernetesHealthChecker;
    private final KubernetesConfigHelper kubernetesConfigHelper;
    private final AopsKubernetesClusterConfigService aopsKubernetesClusterConfigService;

    public void run() {
        List<AopsKubernetesClusterConfig> list = aopsKubernetesClusterConfigService.describeKubernetesClusterConfig();
        if (CollUtil.isEmpty(list)) {
            log.info("没有找到配置信息");
            return;
        }
        for (AopsKubernetesClusterConfig aopsKubernetesClusterConfig : list) {
            try {
                ApiClient apiClient = kubernetesConfigHelper.loadFormContent(aopsKubernetesClusterConfig.getConfigContent());
                kubernetesHealthChecker.checkConfigContent(apiClient);
                aopsKubernetesClusterConfigService.modifyStatusById(aopsKubernetesClusterConfig.getId(), KubernetesResourceHealthStatusEnum.HEALTH);
                log.info("{} K8s服务端健康", aopsKubernetesClusterConfig.getClusterName());
            } catch (Exception e) {
                aopsKubernetesClusterConfigService.modifyStatusById(aopsKubernetesClusterConfig.getId(), KubernetesResourceHealthStatusEnum.UN_HEALTH);
                log.error("{} K8s服务端不健康", aopsKubernetesClusterConfig.getClusterName(), e);
            }
        }
    }
}
