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
import cn.odboy.framework.kubernetes.model.request.KubernetesApiDeploymentRequest;
import cn.odboy.framework.kubernetes.model.request.KubernetesApiPodRequest;
import cn.odboy.framework.kubernetes.model.response.KubernetesApiExceptionResponse;
import cn.odboy.framework.kubernetes.model.response.KubernetesResourceResponse;
import cn.odboy.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsDryRunVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsPrettyVo;
import cn.odboy.framework.kubernetes.util.KubernetesResourceLabelSelectorUtil;
import cn.odboy.framework.kubernetes.util.KubernetesResourceNameUtil;
import cn.odboy.util.ValidationUtil;
import com.alibaba.fastjson2.JSON;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentBuilder;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * K8s Deployment
 *
 * @author odboy
 * @date 2024-10-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KubernetesDeploymentRepository {
    private final KubernetesApiClientManager kubernetesApiClientManager;
    private final KubernetesPodRepository kubernetesPodRepository;

    /**
     * 创建Deployment
     *
     * @param args /
     */
    public V1Deployment createDeployment(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiDeploymentRequest.Create args) {
        ValidationUtil.validate(args);
        try {
            Map<String, String> labels = KubernetesResourceLabelSelectorUtil.getLabelsByAppName(args.getAppName());
            String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue()));
            // 构建deployment的yaml对象
            V1Deployment deployment = new V1DeploymentBuilder()
                    .withNewMetadata()
                    .withName(deploymentName)
                    .withNamespace(args.getAppName())
                    .withAnnotations(args.getAnnotations())
                    .endMetadata()
                    .withNewSpec()
                    .withReplicas(args.getReplicas())
                    .withSelector(new V1LabelSelector().matchLabels(labels))
                    .withNewTemplate()
                    .withNewMetadata()
                    .withLabels(labels)
                    .endMetadata()
                    .withNewSpec()
                    .withContainers(new V1Container()
                            .name(KubernetesResourceNameUtil.getPodName(args.getAppName(), kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue())))
                            .image(args.getImage())
                            .ports(CollUtil.newArrayList(new V1ContainerPort().containerPort(args.getPort())))
                    )
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.createNamespacedDeployment(args.getAppName(), deployment,
                    new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建Deployment失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建Deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建Deployment失败");
        } catch (Exception e) {
            log.error("创建Deployment失败:", e);
            throw new BadRequestException("创建Deployment失败");
        }
    }

    /**
     * 变更Deployment副本数量
     *
     * @param args /
     */
    public V1Deployment changeDeploymentReplicas(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiDeploymentRequest.ChangeReplicas args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue()));
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, args.getAppName(), "false", null, null);
            if (deployment == null || deployment.getSpec() == null) {
                throw new BadRequestException(deploymentName + " 不存在");
            }
            deployment.getSpec().setReplicas(args.getNewReplicas());
            return appsV1Api.replaceNamespacedDeployment(deploymentName, args.getAppName(), deployment, new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更Deployment副本数量失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更Deployment副本数量失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更Deployment副本数量失败");
        } catch (Exception e) {
            log.error("变更Deployment副本数量失败:", e);
            throw new BadRequestException("变更Deployment副本数量失败");
        }
    }

    /**
     * 变更所有Deployment镜像地址
     *
     * @param args /
     */
    public V1Deployment changeDeploymentImage(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiDeploymentRequest.ChangeImage args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue()));
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, args.getAppName(), "false", null, null);
            if (deployment == null || deployment.getSpec() == null) {
                throw new BadRequestException(deploymentName + " 不存在");
            }
            V1PodSpec podSpec = deployment.getSpec().getTemplate().getSpec();
            if (podSpec != null) {
                List<V1Container> containers = deployment.getSpec().getTemplate().getSpec().getContainers();
                if (containers.isEmpty()) {
                    throw new BadRequestException("Pod中不包含任何容器");
                }
                containers.get(0).setImage(args.getNewImage());
            }
            V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(deploymentName, args.getAppName(), deployment, new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null);
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发deployment重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<KubernetesResourceResponse.Pod> podList = kubernetesPodRepository.listPods(clusterCodeVo, args.getAppName(), deploymentName);
            for (KubernetesResourceResponse.Pod pod : podList) {
                if (KubernetesPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    KubernetesApiPodRequest.Rebuild rebuildArgs = new KubernetesApiPodRequest.Rebuild();
                    rebuildArgs.setPodName(pod.getName());
                    rebuildArgs.setNamespace(pod.getNamespace());
                    kubernetesPodRepository.rebuildPod(clusterCodeVo, dryRunVo, rebuildArgs);
                }
            }
            return v1Deployment;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更Deployment镜像地址失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更Deployment镜像地址失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更Deployment镜像地址失败");
        } catch (Exception e) {
            log.error("变更Deployment镜像地址失败:", e);
            throw new BadRequestException("变更Deployment镜像地址失败");
        }
    }

    /**
     * 变更Deployment规格
     *
     * @param args /
     */
    public V1Deployment changeDeploymentSpecs(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiDeploymentRequest.ChangePodSpecs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue()));
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, args.getAppName(), "false", null, null);
            if (deployment == null || deployment.getSpec() == null) {
                throw new BadRequestException(deploymentName + " 不存在");
            }
            V1PodSpec podSpec = deployment.getSpec().getTemplate().getSpec();
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
                                .putRequestsItem("cpu", new Quantity("10m"))
                                .putRequestsItem("memory", new Quantity("1G"))
                                .putLimitsItem("cpu", new Quantity(String.valueOf(args.getCpuNum())))
                                .putLimitsItem("memory", new Quantity(args.getMemoryNum() + "G"));
                        break;
                    }
                }
            }
            V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(deploymentName, args.getAppName(), deployment, new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null);
            List<KubernetesResourceResponse.Pod> pods = kubernetesPodRepository.listPods(clusterCodeVo, args.getAppName(), args.getAppName());
            for (KubernetesResourceResponse.Pod pod : pods) {
                if (KubernetesPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    KubernetesApiPodRequest.Rebuild rebuildArgs = new KubernetesApiPodRequest.Rebuild();
                    rebuildArgs.setNamespace(pod.getNamespace());
                    rebuildArgs.setPodName(pod.getName());
                    kubernetesPodRepository.rebuildPod(clusterCodeVo, dryRunVo, rebuildArgs);
                }
            }
            return v1Deployment;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更Deployment规格失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更Deployment规格失败: {}, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更Deployment规格失败: {}");
        } catch (Exception e) {
            log.error("变更Deployment规格失败:", e);
            throw new BadRequestException("变更Deployment规格失败");
        }
    }

    /**
     * 删除Deployment
     *
     * @param args /
     */
    public V1Status deleteDeployment(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiDeploymentRequest.Delete args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue()));
            return appsV1Api.deleteNamespacedDeployment(deploymentName, args.getAppName(), new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("删除Deployment失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("删除Deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("删除Deployment失败");
        } catch (Exception e) {
            log.error("删除Deployment失败:", e);
            throw new BadRequestException("删除Deployment失败");
        }
    }

    /**
     * 通过appName查询deployment失败
     *
     * @param appName 应用名称
     * @return /
     */
    public V1Deployment describeDeploymentByAppName(ArgsClusterCodeVo clusterCodeVo, String appName) {
        Assert.notNull(clusterCodeVo, "集群编码不能为空");
        Assert.notEmpty(appName, "应用名称不能为空");
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = KubernetesResourceNameUtil.getDeploymentName(appName, clusterCodeVo.getValue());
            return appsV1Api.readNamespacedDeployment(deploymentName, appName, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(KubernetesActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("通过deployment名称查询deployment失败: {}", responseBody, e);
                throw new BadRequestException("通过deployment名称查询deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("通过deployment名称查询deployment失败");
        } catch (Exception e) {
            log.error("通过deployment名称查询deployment失败:", e);
            throw new BadRequestException("通过deployment名称查询deployment失败");
        }
    }

    /**
     * 通过appName查询deployment失败
     *
     * @param name      deployment名称
     * @param namespace 命名空间
     * @return /
     */
    public V1Deployment describeDeploymentByName(ArgsClusterCodeVo clusterCodeVo, String name, String namespace) {
        Assert.notNull(clusterCodeVo, "集群编码不能为空");
        Assert.notEmpty(name, "名称不能为空");
        Assert.notEmpty(namespace, "命名空间不能为空");
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.readNamespacedDeployment(name, namespace, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(KubernetesActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("通过deployment名称查询deployment失败: {}", responseBody, e);
                throw new BadRequestException("通过deployment名称查询deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("通过deployment名称查询deployment失败");
        } catch (Exception e) {
            log.error("通过deployment名称查询deployment失败:", e);
            throw new BadRequestException("通过deployment名称查询deployment失败");
        }
    }

    public V1Deployment loadDeploymentFromYaml(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiDeploymentRequest.LoadFromYaml args) {
        ValidationUtil.validate(args);
        try {
            V1Deployment deployment = Yaml.loadAs(args.getYamlContent(), V1Deployment.class);
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = Objects.requireNonNull(deployment.getMetadata()).getName();
            String namespace = Objects.requireNonNull(deployment.getMetadata()).getNamespace();
            V1Deployment v1Deployment = describeDeploymentByName(clusterCodeVo, deploymentName, namespace);
            if (v1Deployment == null) {
                appsV1Api.createNamespacedDeployment(namespace, deployment, new ArgsPrettyVo(false).getValue(),
                        dryRunVo.getValue(), null);
            } else {
                appsV1Api.replaceNamespacedDeployment(deploymentName, namespace, deployment, new ArgsPrettyVo(false).getValue(),
                        dryRunVo.getValue(), null);
            }
            return deployment;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载Deployment失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载Deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载Deployment失败");
        } catch (Exception e) {
            log.error("从yml加载Deployment失败:", e);
            throw new BadRequestException("从yml加载Deployment失败");
        }
    }
}
