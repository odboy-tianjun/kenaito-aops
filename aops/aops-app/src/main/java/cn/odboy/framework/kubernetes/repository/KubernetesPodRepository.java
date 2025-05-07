/*
 *  Copyright 2021-2025 Tian Jun
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
package cn.odboy.framework.kubernetes.repository;

import cn.odboy.exception.BadRequestException;
import cn.odboy.framework.kubernetes.context.KubernetesApiClientManager;
import cn.odboy.framework.kubernetes.model.args.KubernetesPodApiArgs;
import cn.odboy.framework.kubernetes.model.response.KubernetesApiExceptionResponse;
import cn.odboy.framework.kubernetes.model.response.KubernetesResourceResponse;
import cn.odboy.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsDryRunVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsPrettyVo;
import cn.odboy.framework.kubernetes.util.KubernetesResourceLabelSelectorUtil;
import cn.odboy.util.ValidationUtil;
import com.alibaba.fastjson2.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final KubernetesApiClientManager k8SClientAdmin;

    public List<KubernetesResourceResponse.Pod> listPods(ArgsClusterCodeVo clusterCodeVo, Map<String, String> fieldSelector, Map<String, String> labelSelector) {
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            String fieldSelectorStr = KubernetesResourceLabelSelectorUtil.genLabelSelectorExpression(fieldSelector);
            String labelSelectorStr = KubernetesResourceLabelSelectorUtil.genLabelSelectorExpression(labelSelector);
            V1PodList podList = coreV1Api.listPodForAllNamespaces(null, null, fieldSelectorStr, labelSelectorStr, null, "false", null, null, null, null);
            return podList.getItems().stream().map(pod -> {
                V1ObjectMeta metadata = pod.getMetadata();
                if (metadata != null && metadata.getName() != null) {
                    KubernetesResourceResponse.Pod server = new KubernetesResourceResponse.Pod();
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
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("获取Pod列表失败: {}", responseBody, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取Pod列表失败:", e);
            return new ArrayList<>();
        }
    }

    /**
     * 查询pod列表
     *
     * @param namespace     命名空间
     * @param labelSelector pod标签键值对
     * @return /
     */
    public List<KubernetesResourceResponse.Pod> listPods(ArgsClusterCodeVo clusterCodeVo, @NotNull String namespace, Map<String, String> labelSelector) {
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            String labelSelectorStr = KubernetesResourceLabelSelectorUtil.genLabelSelectorExpression(labelSelector);
            V1PodList podList = coreV1Api.listNamespacedPod(namespace, "false", null, null, null, labelSelectorStr, null, null, null, null, null);
            return podList.getItems().stream().map(pod -> {
                KubernetesResourceResponse.Pod server = new KubernetesResourceResponse.Pod();
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
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("获取Pod列表失败: {}", responseBody, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取Pod列表失败:", e);
            return new ArrayList<>();
        }
    }

    public List<KubernetesResourceResponse.Pod> listPods(ArgsClusterCodeVo clusterCodeVo, @NotNull String namespace, @NotNull String podName) {
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            Map<String, String> labelSelector = KubernetesResourceLabelSelectorUtil.getLabelsByAppName(namespace);
            String labelSelectorStr = KubernetesResourceLabelSelectorUtil.genLabelSelectorExpression(labelSelector);
            V1PodList podList = coreV1Api.listNamespacedPod(namespace, "false", null, null, null, labelSelectorStr, null, null, null, null, null);
            return podList.getItems().stream().map(pod -> {
                V1ObjectMeta metadata = pod.getMetadata();
                if (metadata != null && metadata.getName() != null) {
                    if (metadata.getName().contains(podName)) {
                        KubernetesResourceResponse.Pod server = new KubernetesResourceResponse.Pod();
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
                    }
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("获取Pod列表失败: {}", responseBody, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取Pod列表失败:", e);
            return new ArrayList<>();
        }
    }

    /**
     * 重建/重启Pod, 通过删除Pod重建
     *
     * @param dryRunVo
     * @param args     /
     */
    public void rebuildPod(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesPodApiArgs.Rebuild args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            coreV1Api.deleteNamespacedPod(args.getPodName(), args.getNamespace(),
                    new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("重建Pod失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("重建Pod失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("重建Pod失败");
        } catch (Exception e) {
            log.error("重建Pod失败:", e);
            throw new BadRequestException("重建Pod失败");
        }
    }
}
