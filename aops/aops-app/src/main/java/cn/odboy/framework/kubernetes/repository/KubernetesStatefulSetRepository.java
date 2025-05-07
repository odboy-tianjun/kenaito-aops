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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.odboy.exception.BadRequestException;
import cn.odboy.framework.kubernetes.constant.KubernetesActionReasonCodeEnum;
import cn.odboy.framework.kubernetes.constant.KubernetesPodStatusEnum;
import cn.odboy.framework.kubernetes.context.KubernetesApiClientManager;
import cn.odboy.framework.kubernetes.model.args.KubernetesPodApiArgs;
import cn.odboy.framework.kubernetes.model.args.KubernetesStatefulSetApiArgs;
import cn.odboy.framework.kubernetes.model.response.KubernetesApiExceptionResponse;
import cn.odboy.framework.kubernetes.model.response.KubernetesResourceResponse;
import cn.odboy.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsDryRunVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsPrettyVo;
import cn.odboy.framework.kubernetes.util.KubernetesResourceLabelSelectorUtil;
import cn.odboy.framework.kubernetes.util.KubernetesResourceNameUtil;
import cn.odboy.util.ValidationUtil;
import com.alibaba.fastjson2.JSON;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1Probe;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetBuilder;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.openapi.models.V1TCPSocketAction;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Kubernetes StatefulSet Repository
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KubernetesStatefulSetRepository {
    private final KubernetesApiClientManager k8SClientAdmin;
    private final KubernetesPodRepository kubernetesPodRepository;

    /**
     * 创建statefulset控制器
     *
     * @param args /
     */
    public V1StatefulSet createStatefulSet(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesStatefulSetApiArgs.Create args) {
        ValidationUtil.validate(args);
        try {
            String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(clusterCodeVo.getValue()));
            String podName = KubernetesResourceNameUtil.getPodName(args.getAppName(), k8SClientAdmin.getEnvCode(clusterCodeVo.getValue()));
            Map<String, String> labels = KubernetesResourceLabelSelectorUtil.getLabelsByAppName(args.getAppName());
            V1Container v1Container = new V1Container();
            v1Container.setName(podName);
            v1Container.setImage(args.getImage());
            v1Container.setPorts(CollUtil.newArrayList(new V1ContainerPort().containerPort(args.getPort())));
            // 存活检测
            v1Container.setLivenessProbe(new V1Probe().tcpSocket(new V1TCPSocketAction().port(new IntOrString(args.getPort()))));
            // 就绪检测
            v1Container.setReadinessProbe(new V1Probe().tcpSocket(new V1TCPSocketAction().port(new IntOrString(args.getPort()))));
            v1Container.setResources(new V1ResourceRequirements()
                    .putRequestsItem("cpu", new Quantity(String.valueOf(args.getRequestCpuNum())))
                    .putRequestsItem("memory", new Quantity(args.getRequestMemNum() + "Gi"))
                    .putLimitsItem("cpu", new Quantity(String.valueOf(args.getLimitsCpuNum())))
                    .putLimitsItem("memory", new Quantity(args.getLimitsMemNum() + "Gi"))
            );
            // 构建statefulset的yaml对象
            V1StatefulSet statefulSet = new V1StatefulSetBuilder()
                    .withNewMetadata()
                    .withName(statefulSetName)
                    .withNamespace(args.getAppName())
                    .withAnnotations(args.getAnnotations())
                    .endMetadata()
                    .withNewSpec()
                    .withServiceName(statefulSetName)
                    .withReplicas(args.getReplicas())
                    .withNewSelector()
                    .withMatchLabels(labels)
                    .endSelector()
                    .withNewTemplate()
                    .withNewMetadata()
                    .withLabels(labels)
                    .endMetadata()
                    .withNewSpec()
                    .withContainers(v1Container)
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            AppsV1Api api = new AppsV1Api(apiClient);
            return api.createNamespacedStatefulSet(args.getAppName(), statefulSet, new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建StatefulSet失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建StatefulSet失败");
        } catch (Exception e) {
            log.error("创建StatefulSet失败:", e);
            throw new BadRequestException("创建StatefulSet失败");
        }
    }

    /**
     * 变更StatefulSet副本数量
     *
     * @param args /
     */
    public V1StatefulSet changeStatefulSetReplicas(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesStatefulSetApiArgs.ChangeReplicas args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(clusterCodeVo.getValue()));
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, args.getAppName(), null, null, null);
            if (statefulSet == null || statefulSet.getSpec() == null) {
                throw new BadRequestException(statefulSetName + " 不存在");
            }
            statefulSet.getSpec().setReplicas(args.getNewReplicas());
            return appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getAppName(), statefulSet, new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更StatefulSet副本数量失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更StatefulSet副本数量失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更StatefulSet副本数量失败");
        } catch (Exception e) {
            log.error("变更StatefulSet副本数量失败:", e);
            throw new BadRequestException("变更StatefulSet副本数量失败");
        }
    }

    /**
     * 变更statefulset镜像地址
     *
     * @param args /
     */
    public V1StatefulSet changeStatefulSetImage(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesStatefulSetApiArgs.ChangeImage args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(clusterCodeVo.getValue()));
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, args.getAppName(), null, null, null);
            if (statefulSet == null || statefulSet.getSpec() == null) {
                throw new BadRequestException(statefulSetName + " 不存在");
            }
            V1PodSpec podSpec = statefulSet.getSpec().getTemplate().getSpec();
            if (podSpec != null) {
                List<V1Container> containers = podSpec.getContainers();
                if (containers.isEmpty()) {
                    throw new BadRequestException("Pod中不包含任何容器");
                }
                for (V1Container container : containers) {
                    if (container.getName().contains(args.getAppName())) {
                        container.setImage(args.getNewImage());
                        break;
                    }
                }
            }
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<KubernetesResourceResponse.Pod> podList = kubernetesPodRepository.listPods(clusterCodeVo, args.getAppName(), statefulSetName);
            for (KubernetesResourceResponse.Pod pod : podList) {
                if (KubernetesPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    KubernetesPodApiArgs.Rebuild rebuildArgs = new KubernetesPodApiArgs.Rebuild();
                    rebuildArgs.setPodName(pod.getName());
                    rebuildArgs.setNamespace(pod.getNamespace());
                    kubernetesPodRepository.rebuildPod(clusterCodeVo, dryRunVo, rebuildArgs);
                }
            }
            return appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getAppName(), statefulSet, new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更StatefulSet镜像地址失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更StatefulSet镜像地址失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更StatefulSet镜像地址失败");
        } catch (Exception e) {
            log.error("变更StatefulSet镜像地址失败:", e);
            throw new BadRequestException("变更StatefulSet镜像地址失败");
        }
    }

    /**
     * 变更statefulset规格
     *
     * @param args /
     * @return /
     */
    public V1StatefulSet changeStatefulSetSpecs(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesStatefulSetApiArgs.ChangeSpecs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(clusterCodeVo.getValue()));
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, args.getAppName(), null, null, null);
            if (statefulSet == null || statefulSet.getSpec() == null) {
                throw new BadRequestException(statefulSetName + " 不存在");
            }
            V1PodSpec podSpec = statefulSet.getSpec().getTemplate().getSpec();
            if (podSpec != null) {
                List<V1Container> containers = podSpec.getContainers();
                if (containers.isEmpty()) {
                    throw new BadRequestException("Pod中不包含任何容器");
                }
                for (V1Container container : containers) {
                    if (container.getName().contains(args.getAppName())) {
                        V1ResourceRequirements resources = container.getResources();
                        if (resources == null) {
                            resources = new V1ResourceRequirements();
                        }
                        resources
                                .putRequestsItem("cpu", new Quantity(String.valueOf(args.getRequestCpuNum())))
                                .putRequestsItem("memory", new Quantity(args.getRequestMemNum() + "Gi"))
                                .putLimitsItem("cpu", new Quantity(String.valueOf(args.getLimitsCpuNum())))
                                .putLimitsItem("memory", new Quantity(args.getLimitsMemNum() + "Gi"));
                        break;
                    }
                }
            }
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<KubernetesResourceResponse.Pod> podList = kubernetesPodRepository.listPods(clusterCodeVo, args.getAppName(), statefulSetName);
            for (KubernetesResourceResponse.Pod pod : podList) {
                if (KubernetesPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    KubernetesPodApiArgs.Rebuild rebuildArgs = new KubernetesPodApiArgs.Rebuild();
                    rebuildArgs.setPodName(pod.getName());
                    rebuildArgs.setNamespace(pod.getNamespace());
                    kubernetesPodRepository.rebuildPod(clusterCodeVo, dryRunVo, rebuildArgs);
                }
            }
            return appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getAppName(), statefulSet, new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更StatefulSet镜像地址失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更StatefulSet镜像地址失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更StatefulSet镜像地址失败");
        } catch (Exception e) {
            log.error("变更StatefulSet镜像地址失败:", e);
            throw new BadRequestException("变更StatefulSet镜像地址失败");
        }
    }

    /**
     * 变更statefulset镜像地址
     *
     * @param args /
     */
    public V1StatefulSet changeStatefulSetImageV2(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesStatefulSetApiArgs.ChangeImage args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(clusterCodeVo.getValue()));
            // 这种方式也不能使非Running中的容器重建
            String jsonPatch = "[{\"op\": \"replace\", \"path\": \"/spec/template/spec/containers/0/image\", \"value\": \"" + args.getNewImage() + "\"}]";
            V1Patch patch = new V1Patch(jsonPatch);
            return appsV1Api.patchNamespacedStatefulSet(statefulSetName, args.getAppName(), patch, new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更StatefulSet镜像规格失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更StatefulSet镜像规格失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更StatefulSet镜像规格失败");
        } catch (Exception e) {
            log.error("变更StatefulSet镜像规格失败:", e);
            throw new BadRequestException("变更StatefulSet镜像规格失败");
        }
    }

    /**
     * 删除StatefulSet
     *
     * @param args /
     */
    public V1Status deleteStatefulSet(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesStatefulSetApiArgs.Delete args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(clusterCodeVo.getValue()));
            return appsV1Api.deleteNamespacedStatefulSet(statefulSetName, args.getAppName(), new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("删除StatefulSet失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("删除StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("删除StatefulSet失败");
        } catch (Exception e) {
            log.error("删除StatefulSet失败:", e);
            throw new BadRequestException("删除StatefulSet失败");
        }
    }

    /**
     * 根据appName获取StatefulSet
     *
     * @return /
     */
    public V1StatefulSet describeStatefulSetByAppName(String clusterCode, String appName) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(appName, "应用名称不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(appName, clusterCode);
            return appsV1Api.readNamespacedStatefulSet(statefulSetName, appName, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(KubernetesActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据appName获取StatefulSet失败: {}", responseBody, e);
                throw new BadRequestException("根据appName获取StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据appName获取StatefulSet失败");
        } catch (Exception e) {
            log.error("根据appName获取StatefulSet失败:", e);
            throw new BadRequestException("根据appName获取StatefulSet失败");
        }
    }

    /**
     * 根据name获取StatefulSet
     *
     * @return /
     */
    public V1StatefulSet describeStatefulSetByName(String clusterCode, String name, String namespace) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(name, "名称不能为空");
        Assert.notEmpty(namespace, "命名空间不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.readNamespacedStatefulSet(name, namespace, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(KubernetesActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据name获取StatefulSet失败: {}", responseBody, e);
                throw new BadRequestException("根据name获取StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据name获取StatefulSet失败");
        } catch (Exception e) {
            log.error("根据name获取StatefulSet失败:", e);
            throw new BadRequestException("根据name获取StatefulSet失败");
        }
    }

    /**
     * 从yaml文件内容加载StatefulSet
     *
     * @param args /
     */
    public V1StatefulSet loadStatefulSetFromYaml(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesStatefulSetApiArgs.LoadFromYaml args) {
        ValidationUtil.validate(args);
        try {
            V1StatefulSet statefulSet = Yaml.loadAs(args.getYamlContent(), V1StatefulSet.class);
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = Objects.requireNonNull(statefulSet.getMetadata()).getName();
            String namespace = Objects.requireNonNull(statefulSet.getMetadata()).getNamespace();
            V1StatefulSet statefulSetByName = describeStatefulSetByName(clusterCodeVo.getValue(), statefulSetName, namespace);
            if (statefulSetByName == null) {
                appsV1Api.createNamespacedStatefulSet(namespace, statefulSet, new ArgsPrettyVo(false).getValue(),
                        dryRunVo.getValue(), null);
            } else {
                appsV1Api.replaceNamespacedStatefulSet(statefulSetName, namespace, statefulSet, new ArgsPrettyVo(false).getValue(),
                        dryRunVo.getValue(), null);
            }
            return statefulSet;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载StatefulSet失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载StatefulSet失败");
        } catch (Exception e) {
            log.error("从yml加载StatefulSet失败:", e);
            throw new BadRequestException("从yml加载StatefulSet失败");
        }
    }
}
