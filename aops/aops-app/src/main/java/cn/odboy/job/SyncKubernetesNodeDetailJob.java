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

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.odboy.constant.GlobalEnvEnum;
import cn.odboy.dal.dataobject.AopsKubernetesClusterConfig;
import cn.odboy.dal.dataobject.AopsKubernetesClusterNode;
import cn.odboy.framework.kubernetes.model.response.KubernetesResourceResponse;
import cn.odboy.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.framework.kubernetes.repository.KubernetesNodeRepository;
import cn.odboy.framework.kubernetes.repository.KubernetesPodRepository;
import cn.odboy.service.AopsKubernetesClusterConfigService;
import cn.odboy.service.AopsKubernetesClusterNodeService;
import cn.odboy.util.CollUtil;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeAddress;
import io.kubernetes.client.openapi.models.V1NodeCondition;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1NodeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步集群Node节点
 *
 * @author odboy
 * @date 2024-11-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncKubernetesNodeDetailJob {
    private final AopsKubernetesClusterConfigService containerdClusterConfigService;
    private final AopsKubernetesClusterNodeService containerdClusterNodeService;
    private final KubernetesNodeRepository kubernetesNodeRepository;
    private final KubernetesPodRepository kubernetesPodRepository;

    public void run() {
        List<AopsKubernetesClusterConfig> list = containerdClusterConfigService.describeKubernetesClusterConfigWithHealth();
        if (CollUtil.isEmpty(list)) {
            log.info("没有找到配置信息");
            return;
        }
        List<AopsKubernetesClusterNode> newRecordList = new ArrayList<>();
        List<AopsKubernetesClusterNode> updRecordList = new ArrayList<>();
        for (AopsKubernetesClusterConfig clusterConfig : list) {
            try {
                GlobalEnvEnum envEnum = GlobalEnvEnum.getByCode(clusterConfig.getEnvCode());
                if (envEnum != null) {
                    V1NodeList v1NodeList = kubernetesNodeRepository.describeNodeList(new ArgsClusterCodeVo(clusterConfig.getClusterCode()));
                    List<V1Node> items = v1NodeList.getItems();
                    for (V1Node item : items) {
                        try {
                            injectNodeInfo(new ArgsClusterCodeVo(clusterConfig.getClusterCode()), clusterConfig, item, envEnum, newRecordList, updRecordList);
                        } catch (Exception e) {
                            // 忽略
                        }
                    }
                }
            } catch (Exception e) {
                log.error("集群 {} 异常", clusterConfig.getEnvCode(), e);
            }
        }
        if (CollUtil.isNotEmpty(newRecordList)) {
            try {
                containerdClusterNodeService.saveBatch(newRecordList, 100);
            } catch (Exception e) {
                log.error("批量插入Node信息失败", e);
            }
        }
        if (CollUtil.isNotEmpty(updRecordList)) {
            try {
                containerdClusterNodeService.updateBatchById(updRecordList, 100);
            } catch (Exception e) {
                log.error("批量更新Node信息失败", e);
            }
        }
    }

    private void injectNodeInfo(ArgsClusterCodeVo clusterCodeVo, AopsKubernetesClusterConfig aopsKubernetesClusterConfig, V1Node item, GlobalEnvEnum envEnum,
                                List<AopsKubernetesClusterNode> newRecordList, List<AopsKubernetesClusterNode> updRecordList) throws Exception {
        AopsKubernetesClusterNode clusterNode = new AopsKubernetesClusterNode();
        clusterNode.setClusterConfigId(aopsKubernetesClusterConfig.getId());
        clusterNode.setEnvCode(envEnum.getCode());
        if (item.getMetadata() != null) {
            clusterNode.setNodeName(item.getMetadata().getName());
            if (CollUtil.isNotEmpty(item.getMetadata().getLabels())) {
                item.getMetadata().getLabels().forEach((k, v) -> {
                    if (k.startsWith("kubernetes.io/role")) {
                        clusterNode.setNodeRoles(v);
                    }
                });
            }
            if (item.getMetadata().getCreationTimestamp() != null) {
                clusterNode.setNodeAge(DateUtil.formatBetween(Date.from(item.getMetadata().getCreationTimestamp().toInstant()), new Date(), BetweenFormatter.Level.DAY));
            }
        }
        if (item.getSpec() != null) {
            clusterNode.setNodePodCidr(item.getSpec().getPodCIDR());
        }
        V1NodeStatus status = item.getStatus();
        if (status != null) {
            if (CollUtil.isNotEmpty(status.getConditions())) {
                V1NodeCondition nodeReadyCondition = status.getConditions().stream().filter(f -> "Ready".equals(f.getType())).findFirst().orElse(null);
                if (nodeReadyCondition == null) {
                    clusterNode.setNodeStatus("False");
                } else {
                    clusterNode.setNodeStatus(nodeReadyCondition.getStatus());
                }
            }
            if (status.getNodeInfo() != null) {
                clusterNode.setNodeK8sVersion(status.getNodeInfo().getKubeletVersion());
                clusterNode.setNodeOsImage(status.getNodeInfo().getOsImage());
                clusterNode.setNodeOsKernelVersion(status.getNodeInfo().getKernelVersion());
                clusterNode.setNodeContainerRuntime(status.getNodeInfo().getContainerRuntimeVersion());
                clusterNode.setNodeOsArchitecture(status.getNodeInfo().getArchitecture());
            }
            if (CollUtil.isNotEmpty(status.getAddresses())) {
                clusterNode.setNodeInternalIp(status.getAddresses().stream().filter(f -> "InternalIP".equals(f.getType())).findFirst().orElse(new V1NodeAddress()).getAddress());
                clusterNode.setNodeHostname(status.getAddresses().stream().filter(f -> "Hostname".equals(f.getType())).findFirst().orElse(new V1NodeAddress()).getAddress());
            }
        }
        // 计算pod数量
        Map<String, String> fieldSelector = new HashMap<>(1);
        fieldSelector.put("spec.nodeName", clusterNode.getNodeName());
        List<KubernetesResourceResponse.Pod> podList = kubernetesPodRepository.describePodList(clusterCodeVo, fieldSelector, null);
        // 计算Pod数量
        clusterNode.setNodePodSize(podList.size());
        AopsKubernetesClusterNode kubernetesClusterNode = containerdClusterNodeService.describeKubernetesClusterNodeByArgs(envEnum, clusterNode.getNodeInternalIp());
        if (kubernetesClusterNode == null) {
            newRecordList.add(clusterNode);
        } else {
            clusterNode.setId(kubernetesClusterNode.getId());
            updRecordList.add(clusterNode);
        }
    }
}
