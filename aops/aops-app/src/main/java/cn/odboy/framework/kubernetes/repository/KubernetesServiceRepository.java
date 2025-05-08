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

import cn.odboy.framework.kubernetes.constant.KubernetesResourceLabelEnum;
import cn.odboy.framework.kubernetes.context.KubernetesApiClientManager;
import cn.odboy.framework.kubernetes.exception.KubernetesApiExceptionCatch;
import cn.odboy.framework.kubernetes.model.request.KubernetesApiServiceRequest;
import cn.odboy.framework.kubernetes.model.vo.ArgsAppNameVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsDryRunVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsNamespaceNameVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsPrettyVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsResourceNameVo;
import cn.odboy.framework.kubernetes.util.KubernetesResourceNameUtil;
import cn.odboy.util.ValidationUtil;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * Kubernetes Service Repository
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KubernetesServiceRepository {
    private final KubernetesApiClientManager kubernetesApiClientManager;

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据appName查询Service", throwException = false)
    public V1Service describeServiceByAppName(ArgsClusterCodeVo clusterCodeVo, ArgsAppNameVo appNameVo) {
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String serviceName = KubernetesResourceNameUtil.getServiceName(appNameVo.getValue(), envCode);
        return coreV1Api.readNamespacedService(
                serviceName,
                appNameVo.getValue(),
                new ArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据name和namespace查询Service", throwException = false)
    public V1Service describeServiceByNameWithNamespace(ArgsClusterCodeVo clusterCodeVo, ArgsResourceNameVo resourceNameVo, ArgsNamespaceNameVo namespaceNameVo) {
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        return coreV1Api.readNamespacedService(
                resourceNameVo.getValue(),
                namespaceNameVo.getValue(),
                new ArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "创建Service")
    public V1Service createService(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiServiceRequest.Create args) throws Exception {
        ValidationUtil.validate(args);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String serviceName = KubernetesResourceNameUtil.getServiceName(args.getAppName(), envCode);
        args.getLabelSelector().put(KubernetesResourceLabelEnum.AppName.getCode(), args.getAppName());
        // 构建service的yaml对象
        V1Service service = new V1ServiceBuilder()
                .withNewMetadata()
                .withName(serviceName)
                .withNamespace(args.getAppName())
//                .withAnnotations(args.getAnnotations())
                .endMetadata()
                .withNewSpec()
                .withPorts(new V1ServicePort().protocol("TCP").port(args.getPort()).targetPort(new IntOrString(args.getTargetPort())))
                .withSelector(args.getLabelSelector())
                .endSpec()
                .build();
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
        CoreV1Api api = new CoreV1Api(apiClient);
        return api.createNamespacedService(
                args.getAppName(),
                service,
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "删除Service")
    public void deleteService(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiServiceRequest.Delete args) throws Exception {
        ValidationUtil.validate(args);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String serviceName = KubernetesResourceNameUtil.getServiceName(args.getAppName(), envCode);
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        CoreV1Api api = new CoreV1Api(apiClient);
        api.deleteNamespacedService(
                serviceName,
                args.getAppName(),
                new ArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null,
                null,
                null,
                null
        );
    }


    @KubernetesApiExceptionCatch(description = "从yml加载Service")
    public V1Service loadServiceFromYaml(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiServiceRequest.LoadFromYaml args) throws Exception {
        ValidationUtil.validate(args);
        V1Service v1Service = Yaml.loadAs(args.getYamlContent(), V1Service.class);
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        String serviceName = Objects.requireNonNull(v1Service.getMetadata()).getName();
        String namespace = Objects.requireNonNull(v1Service.getMetadata()).getNamespace();
        V1Service localService = describeServiceByNameWithNamespace(clusterCodeVo, new ArgsResourceNameVo(serviceName), new ArgsNamespaceNameVo(namespace));
        if (localService == null) {
            coreV1Api.createNamespacedService(
                    namespace,
                    v1Service,
                    new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        } else {
            coreV1Api.replaceNamespacedService(
                    serviceName,
                    namespace,
                    v1Service,
                    new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        }
        return v1Service;
    }
}
