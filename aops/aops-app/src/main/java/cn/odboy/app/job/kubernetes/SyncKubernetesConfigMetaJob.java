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
package cn.odboy.app.job.kubernetes;

import cn.odboy.app.dal.dataobject.AopsKubernetesClusterConfigDO;
import cn.odboy.app.framework.kubernetes.core.context.KubernetesConfigHelper;
import cn.odboy.app.framework.kubernetes.core.repository.KubernetesNodeRepository;
import cn.odboy.app.framework.kubernetes.core.repository.KubernetesPodRepository;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesResourceVo;
import cn.odboy.app.service.kubernetes.AopsKubernetesClusterConfigService;
import cn.odboy.common.constant.GlobalEnvEnum;
import cn.odboy.common.util.CollUtil;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步Kubernetes集群配置元数据
 *
 * @author odboy
 * @date 2024-11-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncKubernetesConfigMetaJob {
    private final KubernetesNodeRepository kubernetesNodeRepository;
    private final KubernetesPodRepository kubernetesPodRepository;
    private final KubernetesConfigHelper kubernetesConfigHelper;
    private final AopsKubernetesClusterConfigService aopsKubernetesClusterConfigService;

    public void run() {
        List<AopsKubernetesClusterConfigDO> list = aopsKubernetesClusterConfigService.describeKubernetesClusterConfig();
        if (CollUtil.isEmpty(list)) {
            log.info("没有找到配置信息");
            return;
        }
        for (AopsKubernetesClusterConfigDO clusterConfig : list) {
            try {
                ApiClient apiClient = kubernetesConfigHelper.loadFormContent(clusterConfig.getClusterConfigContent());
                GlobalEnvEnum envEnum = GlobalEnvEnum.getByCode(clusterConfig.getEnvCode());
                if (envEnum != null) {
                    int clusterPodSize = 0;
                    V1NodeList v1NodeList = kubernetesNodeRepository.describeNodeList(apiClient);
                    List<V1Node> items = v1NodeList.getItems();
                    int clusterNodeSize = items.size();
                    for (V1Node item : items) {
                        // 计算pod数量
                        Map<String, String> fieldSelector = new HashMap<>(1);
                        fieldSelector.put("spec.nodeName", item.getMetadata().getName());
                        List<KubernetesResourceVo.Pod> podList = kubernetesPodRepository.describePodList(apiClient, fieldSelector, null);
                        // 计算Pod数量
                        clusterPodSize = clusterPodSize + podList.size();
                    }
                    clusterConfig.setClusterNodeSize(clusterNodeSize);
                    clusterConfig.setClusterPodSize(clusterPodSize);
                    aopsKubernetesClusterConfigService.modifyMetaById(clusterConfig);
                }
            } catch (Exception e) {
//                aopsKubernetesClusterConfigService.modifyStatusById(aopsKubernetesClusterConfigDO.getId(), KubernetesResourceHealthStatusEnum.UN_HEALTH);
//                log.error("{} K8s服务端不健康", aopsKubernetesClusterConfigDO.getClusterName(), e);
            }
        }
    }
}
