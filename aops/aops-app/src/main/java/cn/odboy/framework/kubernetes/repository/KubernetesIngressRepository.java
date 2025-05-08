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
package cn.odboy.framework.kubernetes.repository;

import cn.odboy.framework.kubernetes.context.KubernetesApiClientManager;
import cn.odboy.framework.kubernetes.exception.KubernetesApiExceptionCatch;
import cn.odboy.framework.kubernetes.model.request.KubernetesApiIngressRequest;
import cn.odboy.framework.kubernetes.model.vo.ArgsAppNameVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsDryRunVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsNamespaceNameVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsPrettyVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsResourceNameVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsYamlVo;
import cn.odboy.framework.kubernetes.util.KubernetesResourceNameUtil;
import cn.odboy.util.ValidationUtil;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.V1HTTPIngressPathBuilder;
import io.kubernetes.client.openapi.models.V1HTTPIngressRuleValueBuilder;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1IngressBackendBuilder;
import io.kubernetes.client.openapi.models.V1IngressBuilder;
import io.kubernetes.client.openapi.models.V1IngressServiceBackendBuilder;
import io.kubernetes.client.openapi.models.V1ServiceBackendPortBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * Kubernetes Ingress Repository
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KubernetesIngressRepository {
    private final KubernetesApiClientManager kubernetesApiClientManager;

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据appName查询Ingress", throwException = false)
    public V1Ingress describeIngressByAppName(ArgsClusterCodeVo clusterCodeVo, ArgsAppNameVo appNameVo) {
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String ingressName = KubernetesResourceNameUtil.getIngressName(appNameVo.getValue(), envCode);
        return networkingV1Api.readNamespacedIngress(
                ingressName,
                appNameVo.getValue(),
                new ArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }


    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据ingress名称和namespace查询Ingress", throwException = false)
    public V1Ingress describeIngressByName(ArgsClusterCodeVo clusterCodeVo, ArgsResourceNameVo resourceNameVo, ArgsNamespaceNameVo namespaceNameVo) {
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
        return networkingV1Api.readNamespacedIngress(
                resourceNameVo.getValue(),
                namespaceNameVo.getValue(),
                new ArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "创建Ingress")
    public V1Ingress createIngress(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiIngressRequest.Create args) throws Exception {
        ValidationUtil.validate(args);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        // 构建ingress的yaml对象
        V1Ingress ingress = new V1IngressBuilder()
                .withNewMetadata()
                .withName(KubernetesResourceNameUtil.getIngressName(args.getAppName(), envCode))
                .withNewNamespace(args.getAppName())
//                .withAnnotations(args.getAnnotations())
                .endMetadata()
                .withNewSpec()
                .addNewRule()
                .withHost(args.getHostname())
                .withHttp(new V1HTTPIngressRuleValueBuilder().addToPaths(new V1HTTPIngressPathBuilder()
                        .withPath(args.getPath())
                        .withPathType("ImplementationSpecific")
                        .withBackend(new V1IngressBackendBuilder()
                                .withService(new V1IngressServiceBackendBuilder()
                                        .withName(args.getServiceName())
                                        .withPort(new V1ServiceBackendPortBuilder()
                                                .withNumber(args.getServicePort()).build()).build()).build()).build()).build())
                .endRule()
                .endSpec()
                .build();
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
        return networkingV1Api.createNamespacedIngress(
                args.getAppName(),
                ingress,
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "删除Ingress")
    public void deleteIngress(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiIngressRequest.Delete args) throws Exception {
        ValidationUtil.validate(args);
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String ingressName = KubernetesResourceNameUtil.getIngressName(args.getAppName(), envCode);
        NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
        networkingV1Api.deleteNamespacedIngress(
                ingressName,
                args.getAppName(),
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null,
                null,
                null,
                null
        );
    }


    @KubernetesApiExceptionCatch(description = "从yml加载Ingress")
    public V1Ingress loadIngressFromYaml(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, ArgsYamlVo<V1Ingress> yamlVo) throws Exception {
        V1Ingress ingress = yamlVo.getTarget();
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
        String ingressName = Objects.requireNonNull(ingress.getMetadata()).getName();
        String namespace = Objects.requireNonNull(ingress.getMetadata()).getNamespace();
        V1Ingress v1Ingress = describeIngressByName(
                clusterCodeVo,
                new ArgsResourceNameVo(ingressName),
                new ArgsNamespaceNameVo(namespace)
        );
        if (v1Ingress == null) {
            networkingV1Api.createNamespacedIngress(
                    namespace,
                    ingress,
                    new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        } else {
            networkingV1Api.replaceNamespacedIngress(
                    ingressName,
                    namespace,
                    ingress,
                    new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        }
        return ingress;
    }
}
