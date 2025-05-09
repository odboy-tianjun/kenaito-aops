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
import cn.odboy.app.framework.kubernetes.core.context.KubernetesApiClientManager;
import cn.odboy.app.framework.kubernetes.core.exception.KubernetesApiExceptionCatch;
import cn.odboy.app.framework.kubernetes.core.util.KubernetesResourceLabelMetaUtil;
import cn.odboy.app.framework.kubernetes.core.util.KubernetesResourceNameUtil;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesApiPodArgs;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesApiStatefulSetArgs;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesResourceVo;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsAppNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsClusterCodeVo;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsDryRunVo;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsNamespaceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsResourceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsYamlVo;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.app.framework.kubernetes.core.constant.KubernetesPodStatusEnum;
import cn.odboy.app.framework.kubernetes.core.vo.ArgsPrettyVo;
import cn.odboy.common.util.ValidationUtil;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    private final KubernetesApiClientManager kubernetesApiClientManager;
    private final KubernetesPodRepository kubernetesPodRepository;

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据appName查询StatefulSet", throwException = false)
    public V1StatefulSet describeStatefulSetByAppName(ArgsClusterCodeVo clusterCodeVo, ArgsAppNameVo appNameVo) {
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(appNameVo.getValue(), envCode);
        return appsV1Api.readNamespacedStatefulSet(
                statefulSetName,
                appNameVo.getValue(),
                new ArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据name和namespace查询StatefulSet", throwException = false)
    public V1StatefulSet describeStatefulSetByNameWithNamespace(ArgsClusterCodeVo clusterCodeVo, ArgsResourceNameVo resourceNameVo, ArgsNamespaceNameVo namespaceNameVo) {
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        return appsV1Api.readNamespacedStatefulSet(
                resourceNameVo.getValue(),
                namespaceNameVo.getValue(),
                new ArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "创建StatefulSet")
    public V1StatefulSet createStatefulSet(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiStatefulSetArgs.Create args) throws Exception {
        ValidationUtil.validate(args);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), envCode);
        String podName = KubernetesResourceNameUtil.getPodName(args.getAppName(), envCode);
        Map<String, String> labels = KubernetesResourceLabelMetaUtil.getLabelsByAppName(args.getAppName());
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
//                .withAnnotations(args.getAnnotations())
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
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        AppsV1Api api = new AppsV1Api(apiClient);
        return api.createNamespacedStatefulSet(
                args.getAppName(),
                statefulSet,
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "变更StatefulSet副本数量")
    public V1StatefulSet changeStatefulSetReplicas(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiStatefulSetArgs.ChangeReplicas args) throws Exception {
        ValidationUtil.validate(args);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), envCode);
        V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(
                statefulSetName,
                args.getAppName(),
                new ArgsPrettyVo(false).getValue(),
                null,
                null
        );
        if (statefulSet == null || statefulSet.getSpec() == null) {
            throw new BadRequestException(statefulSetName + " 不存在");
        }
        statefulSet.getSpec().setReplicas(args.getNewReplicas());
        return appsV1Api.replaceNamespacedStatefulSet(
                statefulSetName,
                args.getAppName(),
                statefulSet,
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "变更StatefulSet镜像地址")
    public V1StatefulSet changeStatefulSetImage(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiStatefulSetArgs.ChangeImage args) throws Exception {
        ValidationUtil.validate(args);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), envCode);
        V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(
                statefulSetName,
                args.getAppName(),
                new ArgsPrettyVo(false).getValue(),
                null,
                null
        );
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
        List<KubernetesResourceVo.Pod> podList = kubernetesPodRepository.describePodListByNameWithNamespace(
                clusterCodeVo,
                new ArgsNamespaceNameVo(args.getAppName()),
                new ArgsResourceNameVo(statefulSetName)
        );
        for (KubernetesResourceVo.Pod pod : podList) {
            if (KubernetesPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                KubernetesApiPodArgs.Rebuild rebuildArgs = new KubernetesApiPodArgs.Rebuild();
                rebuildArgs.setPodName(pod.getName());
                rebuildArgs.setNamespace(pod.getNamespace());
                kubernetesPodRepository.rebuildPod(clusterCodeVo, dryRunVo, rebuildArgs);
            }
        }
        return appsV1Api.replaceNamespacedStatefulSet(
                statefulSetName,
                args.getAppName(),
                statefulSet,
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "变更StatefulSet规格")
    public V1StatefulSet changeStatefulSetSpecs(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiStatefulSetArgs.ChangeSpecs args) throws Exception {
        ValidationUtil.validate(args);
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), envCode);
        V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(
                statefulSetName,
                args.getAppName(),
                new ArgsPrettyVo(false).getValue(),
                null,
                null
        );
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
        /// 这里手动删除的原因是：改变image路径并没有触发StatefulSet重建, 那只能出此下策
        /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
        /// 事实表明, 只有处于running中的Pod才会正常的重建
        List<KubernetesResourceVo.Pod> podList = kubernetesPodRepository.describePodListByNameWithNamespace(
                clusterCodeVo,
                new ArgsNamespaceNameVo(args.getAppName()),
                new ArgsResourceNameVo(statefulSetName)
        );
        for (KubernetesResourceVo.Pod pod : podList) {
            if (KubernetesPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                KubernetesApiPodArgs.Rebuild rebuildArgs = new KubernetesApiPodArgs.Rebuild();
                rebuildArgs.setPodName(pod.getName());
                rebuildArgs.setNamespace(pod.getNamespace());
                kubernetesPodRepository.rebuildPod(clusterCodeVo, dryRunVo, rebuildArgs);
            }
        }
        return appsV1Api.replaceNamespacedStatefulSet(
                statefulSetName,
                args.getAppName(),
                statefulSet,
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "变更StatefulSet镜像地址V2")
    public V1StatefulSet changeStatefulSetImageV2(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiStatefulSetArgs.ChangeImage args) throws Exception {
        ValidationUtil.validate(args);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), envCode);
        // 这种方式也不能使非Running中的容器重建
        String jsonPatch = "[{\"op\": \"replace\", \"path\": \"/spec/template/spec/containers/0/image\", \"value\": \"" + args.getNewImage() + "\"}]";
        V1Patch patch = new V1Patch(jsonPatch);
        return appsV1Api.patchNamespacedStatefulSet(
                statefulSetName,
                args.getAppName(),
                patch,
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null,
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "删除StatefulSet")
    public V1Status deleteStatefulSet(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiStatefulSetArgs.Delete args) throws Exception {
        ValidationUtil.validate(args);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String statefulSetName = KubernetesResourceNameUtil.getStatefulSetName(args.getAppName(), envCode);
        return appsV1Api.deleteNamespacedStatefulSet(
                statefulSetName,
                args.getAppName(),
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null,
                null,
                null,
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "从yml加载StatefulSet")
    public V1StatefulSet loadStatefulSetFromYaml(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, ArgsYamlVo<V1StatefulSet> yamlVo) throws Exception {
        V1StatefulSet statefulSet = yamlVo.getTarget();
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String statefulSetName = Objects.requireNonNull(statefulSet.getMetadata()).getName();
        String namespace = Objects.requireNonNull(statefulSet.getMetadata()).getNamespace();
        V1StatefulSet statefulSetByName = describeStatefulSetByNameWithNamespace(clusterCodeVo, new ArgsResourceNameVo(statefulSetName), new ArgsNamespaceNameVo(namespace));
        if (statefulSetByName == null) {
            appsV1Api.createNamespacedStatefulSet(
                    namespace,
                    statefulSet,
                    new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        } else {
            appsV1Api.replaceNamespacedStatefulSet(
                    statefulSetName,
                    namespace,
                    statefulSet,
                    new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        }
        return statefulSet;
    }
}
