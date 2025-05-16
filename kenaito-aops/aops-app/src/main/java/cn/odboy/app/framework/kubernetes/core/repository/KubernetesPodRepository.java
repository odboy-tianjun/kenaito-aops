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
package cn.odboy.app.framework.kubernetes.core.repository;

import cn.odboy.app.framework.kubernetes.core.exception.KubernetesApiExceptionCatch;
import cn.odboy.app.framework.kubernetes.core.util.KubernetesResourceLabelMetaUtil;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsDryRunVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsNamespaceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsPrettyVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsResourceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesApiPodArgs;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesResourceVo;
import cn.odboy.common.util.ValidationUtil;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kubernetes Pod Repository
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KubernetesPodRepository {

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "查询pod列表", throwException = false)
    public List<KubernetesResourceVo.Pod> describePodList(ApiClient apiClient, Map<String, String> fieldSelector, Map<String, String> labelSelector) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        String fieldSelectorStr = KubernetesResourceLabelMetaUtil.genLabelSelectorExpression(fieldSelector);
        String labelSelectorStr = KubernetesResourceLabelMetaUtil.genLabelSelectorExpression(labelSelector);
        V1PodList podList = coreV1Api.listPodForAllNamespaces(
                null,
                null,
                fieldSelectorStr,
                labelSelectorStr,
                null,
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null,
                null,
                null
        );
        return podList.getItems().stream()
                .filter(f -> f.getMetadata() != null && f.getMetadata().getName() != null)
                .map(pod -> {
                    V1ObjectMeta metadata = pod.getMetadata();
                    KubernetesResourceVo.Pod server = new KubernetesResourceVo.Pod();
                    if (metadata.getCreationTimestamp() != null) {
                        server.setCreateTime(Date.from(metadata.getCreationTimestamp().toInstant()));
                    }
                    if (metadata.getDeletionTimestamp() != null) {
                        // 当Pod被删除时, 这个属性有值
                        server.setDeleteTime(Date.from(metadata.getDeletionTimestamp().toInstant()));
                    }
                    server.setName(metadata.getName());
                    server.setLabels(metadata.getLabels());
                    server.setNamespace(metadata.getNamespace());
                    server.setResourceVersion(metadata.getResourceVersion());
                    V1PodSpec spec = pod.getSpec();
                    if (spec != null) {
                        server.setRestartPolicy(spec.getRestartPolicy());
                    }
                    V1PodStatus podStatus = pod.getStatus();
                    if (podStatus != null) {
                        server.setIp(podStatus.getPodIP());
                        server.setStatus(server.getDeleteTime() != null ? "Terminated" : podStatus.getPhase());
                        server.setQosClass(podStatus.getQosClass());
                        if (podStatus.getStartTime() != null) {
                            server.setStartTime(Date.from(podStatus.getStartTime().toInstant()));
                        }
                        server.setConditions(podStatus.getConditions());
                    }
                    return server;
                }).collect(Collectors.toList());
    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据namespace查询pod列表", throwException = false)
    public List<KubernetesResourceVo.Pod> describePodListByNamespace(ApiClient apiClient, CustomArgsNamespaceNameVo namespaceNameVo, Map<String, String> labelSelector) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        String labelSelectorStr = KubernetesResourceLabelMetaUtil.genLabelSelectorExpression(labelSelector);
        V1PodList podList = coreV1Api.listNamespacedPod(
                namespaceNameVo.getValue(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null,
                null,
                labelSelectorStr,
                null,
                null,
                null,
                null,
                null
        );
        return podList.getItems().stream().map(pod -> {
            KubernetesResourceVo.Pod server = new KubernetesResourceVo.Pod();
            V1ObjectMeta metadata = pod.getMetadata();
            if (metadata != null) {
                if (metadata.getCreationTimestamp() != null) {
                    server.setCreateTime(Date.from(metadata.getCreationTimestamp().toInstant()));
                }
                if (metadata.getDeletionTimestamp() != null) {
                    // 当Pod被删除时, 这个属性有值
                    server.setDeleteTime(Date.from(metadata.getDeletionTimestamp().toInstant()));
                }
                server.setName(metadata.getName());
                server.setLabels(metadata.getLabels());
                server.setNamespace(metadata.getNamespace());
                server.setResourceVersion(metadata.getResourceVersion());
            }
            V1PodSpec spec = pod.getSpec();
            if (spec != null) {
                server.setRestartPolicy(spec.getRestartPolicy());
            }
            V1PodStatus podStatus = pod.getStatus();
            if (podStatus != null) {
                server.setIp(podStatus.getPodIP());
                server.setStatus(server.getDeleteTime() != null ? "Terminated" : podStatus.getPhase());
                server.setQosClass(podStatus.getQosClass());
                if (podStatus.getStartTime() != null) {
                    server.setStartTime(Date.from(podStatus.getStartTime().toInstant()));
                }
                server.setConditions(podStatus.getConditions());
            }
            return server;
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据name和namespace查询pod列表", throwException = false)
    public List<KubernetesResourceVo.Pod> describePodListByNameWithNamespace(ApiClient apiClient, CustomArgsNamespaceNameVo namespaceNameVo, CustomArgsResourceNameVo resourceNameVo) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        Map<String, String> labelSelector = KubernetesResourceLabelMetaUtil.getLabelsByAppName(namespaceNameVo.getValue());
        String labelSelectorStr = KubernetesResourceLabelMetaUtil.genLabelSelectorExpression(labelSelector);
        V1PodList podList = coreV1Api.listNamespacedPod(
                namespaceNameVo.getValue(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null,
                null,
                labelSelectorStr,
                null,
                null,
                null,
                null,
                null
        );
        return podList.getItems().stream()
                .filter(f -> f.getMetadata() != null && f.getMetadata().getName() != null && f.getMetadata().getName().contains(resourceNameVo.getValue()))
                .map(pod -> {
                    V1ObjectMeta metadata = pod.getMetadata();
                    KubernetesResourceVo.Pod server = new KubernetesResourceVo.Pod();
                    if (metadata.getCreationTimestamp() != null) {
                        server.setCreateTime(Date.from(metadata.getCreationTimestamp().toInstant()));
                    }
                    if (metadata.getDeletionTimestamp() != null) {
                        // 当Pod被删除时, 这个属性有值
                        server.setDeleteTime(Date.from(metadata.getDeletionTimestamp().toInstant()));
                    }
                    server.setName(metadata.getName());
                    server.setLabels(metadata.getLabels());
                    server.setNamespace(metadata.getNamespace());
                    server.setResourceVersion(metadata.getResourceVersion());
                    V1PodSpec spec = pod.getSpec();
                    if (spec != null) {
                        server.setRestartPolicy(spec.getRestartPolicy());
                    }
                    V1PodStatus podStatus = pod.getStatus();
                    if (podStatus != null) {
                        server.setIp(podStatus.getPodIP());
                        server.setStatus(server.getDeleteTime() != null ? "Terminated" : podStatus.getPhase());
                        server.setQosClass(podStatus.getQosClass());
                        if (podStatus.getStartTime() != null) {
                            server.setStartTime(Date.from(podStatus.getStartTime().toInstant()));
                        }
                        server.setConditions(podStatus.getConditions());
                    }
                    return server;
                }).collect(Collectors.toList());
    }

    @KubernetesApiExceptionCatch(description = "重建/重启Pod, 通过删除Pod重建")
    public void rebuildPod(ApiClient apiClient, CustomArgsDryRunVo dryRunVo, KubernetesApiPodArgs.Rebuild args) throws Exception {
        ValidationUtil.validate(args);
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        coreV1Api.deleteNamespacedPod(
                args.getPodName(),
                args.getNamespace(),
                new CustomArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null,
                null,
                null,
                null
        );
    }
}
