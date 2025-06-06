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

import cn.odboy.app.framework.kubernetes.core.constant.KubernetesResourceLabelEnum;
import cn.odboy.app.framework.kubernetes.core.context.KubernetesApiClientManager;
import cn.odboy.app.framework.kubernetes.core.exception.KubernetesApiExceptionCatch;
import cn.odboy.app.framework.kubernetes.core.util.KubernetesResourceNameUtil;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsAppNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsClusterCodeVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsDryRunVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsNamespaceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsPrettyVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsResourceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsYamlVo;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesApiServiceArgs;
import cn.odboy.common.util.ValidationUtil;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.openapi.models.V1ServicePort;
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
    public V1Service describeServiceByAppName(ApiClient apiClient, CustomArgsClusterCodeVo clusterCodeVo, CustomArgsAppNameVo appNameVo) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String serviceName = KubernetesResourceNameUtil.getServiceName(appNameVo.getValue(), envCode);
        return coreV1Api.readNamespacedService(
                serviceName,
                appNameVo.getValue(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据name和namespace查询Service", throwException = false)
    public V1Service describeServiceByNameWithNamespace(ApiClient apiClient, CustomArgsResourceNameVo resourceNameVo, CustomArgsNamespaceNameVo namespaceNameVo) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        return coreV1Api.readNamespacedService(
                resourceNameVo.getValue(),
                namespaceNameVo.getValue(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "创建Service")
    public V1Service createService(ApiClient apiClient, CustomArgsClusterCodeVo clusterCodeVo, CustomArgsDryRunVo dryRunVo, KubernetesApiServiceArgs.Create args) throws Exception {
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
        // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
        CoreV1Api api = new CoreV1Api(apiClient);
        return api.createNamespacedService(
                args.getAppName(),
                service,
                new CustomArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
    }

    @KubernetesApiExceptionCatch(description = "删除Service")
    public void deleteService(ApiClient apiClient, CustomArgsClusterCodeVo clusterCodeVo, CustomArgsDryRunVo dryRunVo, KubernetesApiServiceArgs.Delete args) throws Exception {
        ValidationUtil.validate(args);
        String envCode = kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue());
        String serviceName = KubernetesResourceNameUtil.getServiceName(args.getAppName(), envCode);
        CoreV1Api api = new CoreV1Api(apiClient);
        api.deleteNamespacedService(
                serviceName,
                args.getAppName(),
                new CustomArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null,
                null,
                null,
                null
        );
    }


    @KubernetesApiExceptionCatch(description = "从yml加载Service")
    public V1Service loadServiceFromYaml(ApiClient apiClient, CustomArgsDryRunVo dryRunVo, CustomArgsYamlVo<V1Service> yamlVo) throws Exception {
        V1Service v1Service = yamlVo.getTarget();
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        String serviceName = Objects.requireNonNull(v1Service.getMetadata()).getName();
        String namespace = Objects.requireNonNull(v1Service.getMetadata()).getNamespace();
        V1Service localService = describeServiceByNameWithNamespace(apiClient, new CustomArgsResourceNameVo(serviceName), new CustomArgsNamespaceNameVo(namespace));
        if (localService == null) {
            coreV1Api.createNamespacedService(
                    namespace,
                    v1Service,
                    new CustomArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        } else {
            coreV1Api.replaceNamespacedService(
                    serviceName,
                    namespace,
                    v1Service,
                    new CustomArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        }
        return v1Service;
    }
}
