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

import cn.hutool.core.collection.CollUtil;
import cn.odboy.app.framework.kubernetes.core.constant.KubernetesPodStatusEnum;
import cn.odboy.app.framework.kubernetes.core.context.KubernetesApiClientManager;
import cn.odboy.app.framework.kubernetes.core.exception.KubernetesApiExceptionCatch;
import cn.odboy.app.framework.kubernetes.core.util.KubernetesResourceLabelMetaUtil;
import cn.odboy.app.framework.kubernetes.core.util.KubernetesResourceNameUtil;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsAppNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsClusterCodeVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsDryRunVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsNamespaceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsPrettyVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsResourceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesApiDeploymentArgs;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesApiPodArgs;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesResourceVo;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.util.ValidationUtil;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
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
import lombok.SneakyThrows;
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

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据appName查询deployment", throwException = false)
    public V1Deployment describeDeploymentByAppName(ApiClient apiClient, CustomArgsClusterCodeVo clusterCodeVo, CustomArgsAppNameVo appNameVo) {
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String deploymentName = KubernetesResourceNameUtil.getDeploymentName(appNameVo.getValue(), envCode);
        return appsV1Api.readNamespacedDeployment(
                deploymentName,
                appNameVo.getValue(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据deployment名称和namespace查询deployment", throwException = false)
    public V1Deployment describeDeploymentByNameWithNamespace(ApiClient apiClient, CustomArgsResourceNameVo resourceNameVo, CustomArgsNamespaceNameVo namespaceNameVo) {
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        return appsV1Api.readNamespacedDeployment(
                resourceNameVo.getValue(),
                namespaceNameVo.getValue(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "创建Deployment")
    public V1Deployment createDeployment(ApiClient apiClient, CustomArgsClusterCodeVo clusterCodeVo, CustomArgsDryRunVo dryRunVo, KubernetesApiDeploymentArgs.Create args) throws Exception {
        ValidationUtil.validate(args);
        Map<String, String> labels = KubernetesResourceLabelMetaUtil.getLabelsByAppName(args.getAppName());
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), envCode);
        String podName = KubernetesResourceNameUtil.getPodName(args.getAppName(), envCode);
        // 构建deployment的yaml对象
        V1Deployment deployment = new V1DeploymentBuilder()
                .withNewMetadata()
                .withName(deploymentName)
                .withNamespace(args.getAppName())
//                .withAnnotations(args.getAnnotations())
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
                        .name(podName)
                        .image(args.getImage())
                        .ports(CollUtil.newArrayList(new V1ContainerPort().containerPort(args.getPort())))
                )
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        return appsV1Api.createNamespacedDeployment(
                args.getAppName(),
                deployment,
                new CustomArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "变更Deployment副本数量")
    public V1Deployment changeDeploymentReplicas(ApiClient apiClient, CustomArgsClusterCodeVo clusterCodeVo, CustomArgsDryRunVo dryRunVo, KubernetesApiDeploymentArgs.ChangeReplicas args) throws Exception {
        ValidationUtil.validate(args);
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), envCode);
        V1Deployment deployment = appsV1Api.readNamespacedDeployment(
                deploymentName,
                args.getAppName(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null
        );
        if (deployment == null || deployment.getSpec() == null) {
            throw new BadRequestException(deploymentName + " 不存在");
        }
        deployment.getSpec().setReplicas(args.getNewReplicas());
        return appsV1Api.replaceNamespacedDeployment(
                deploymentName,
                args.getAppName(),
                deployment,
                new CustomArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "变更Deployment镜像地址")
    public V1Deployment changeDeploymentImage(ApiClient apiClient, CustomArgsClusterCodeVo clusterCodeVo, CustomArgsDryRunVo dryRunVo, KubernetesApiDeploymentArgs.ChangeImage args) throws Exception {
        ValidationUtil.validate(args);
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), envCode);
        V1Deployment deployment = appsV1Api.readNamespacedDeployment(
                deploymentName,
                args.getAppName(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null
        );
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
        V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(
                deploymentName,
                args.getAppName(),
                deployment,
                new CustomArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
        // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
        /// 这里手动删除的原因是：改变image路径并没有触发deployment重建, 那只能出此下策
        /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
        /// 事实表明, 只有处于running中的Pod才会正常的重建
        List<KubernetesResourceVo.Pod> podList = kubernetesPodRepository.describePodListByNameWithNamespace(
                apiClient,
                new CustomArgsNamespaceNameVo(args.getAppName()),
                new CustomArgsResourceNameVo(deploymentName)
        );
        for (KubernetesResourceVo.Pod pod : podList) {
            if (KubernetesPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                KubernetesApiPodArgs.Rebuild rebuildArgs = new KubernetesApiPodArgs.Rebuild();
                rebuildArgs.setPodName(pod.getName());
                rebuildArgs.setNamespace(pod.getNamespace());
                kubernetesPodRepository.rebuildPod(apiClient, dryRunVo, rebuildArgs);
            }
        }
        return v1Deployment;
    }

    @KubernetesApiExceptionCatch(description = "变更Deployment规格")
    public V1Deployment changeDeploymentSpecs(ApiClient apiClient, CustomArgsClusterCodeVo clusterCodeVo, CustomArgsDryRunVo dryRunVo, KubernetesApiDeploymentArgs.ChangePodSpecs args) throws Exception {
        ValidationUtil.validate(args);
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), envCode);
        V1Deployment deployment = appsV1Api.readNamespacedDeployment(
                deploymentName,
                args.getAppName(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null
        );
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
        V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(
                deploymentName,
                args.getAppName(),
                deployment,
                new CustomArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
        List<KubernetesResourceVo.Pod> pods = kubernetesPodRepository.describePodListByNameWithNamespace(
                apiClient,
                new CustomArgsNamespaceNameVo(args.getAppName()),
                new CustomArgsResourceNameVo(deploymentName)
        );
        for (KubernetesResourceVo.Pod pod : pods) {
            if (KubernetesPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                KubernetesApiPodArgs.Rebuild rebuildArgs = new KubernetesApiPodArgs.Rebuild();
                rebuildArgs.setNamespace(pod.getNamespace());
                rebuildArgs.setPodName(pod.getName());
                kubernetesPodRepository.rebuildPod(apiClient, dryRunVo, rebuildArgs);
            }
        }
        return v1Deployment;
    }

    @KubernetesApiExceptionCatch(description = "删除Deployment")
    public V1Status deleteDeployment(ApiClient apiClient, CustomArgsClusterCodeVo clusterCodeVo, CustomArgsDryRunVo dryRunVo, KubernetesApiDeploymentArgs.Delete args) throws Exception {
        ValidationUtil.validate(args);
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String deploymentName = KubernetesResourceNameUtil.getDeploymentName(args.getAppName(), envCode);
        return appsV1Api.deleteNamespacedDeployment(
                deploymentName,
                args.getAppName(),
                new CustomArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null,
                null,
                null,
                null
        );
    }


    @KubernetesApiExceptionCatch(description = "从yml加载Deployment")
    public V1Deployment loadDeploymentFromYaml(ApiClient apiClient, CustomArgsDryRunVo dryRunVo, KubernetesApiDeploymentArgs.LoadFromYaml args) throws Exception {
        ValidationUtil.validate(args);
        V1Deployment deployment = Yaml.loadAs(args.getYamlContent(), V1Deployment.class);
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String deploymentName = Objects.requireNonNull(deployment.getMetadata()).getName();
        String namespace = Objects.requireNonNull(deployment.getMetadata()).getNamespace();
        V1Deployment v1Deployment = describeDeploymentByNameWithNamespace(
                apiClient,
                new CustomArgsResourceNameVo(deploymentName),
                new CustomArgsNamespaceNameVo(namespace)
        );
        if (v1Deployment == null) {
            appsV1Api.createNamespacedDeployment(
                    namespace,
                    deployment,
                    new CustomArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        } else {
            appsV1Api.replaceNamespacedDeployment(
                    deploymentName,
                    namespace,
                    deployment,
                    new CustomArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        }
        return deployment;
    }
}
