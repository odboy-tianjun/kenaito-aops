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
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsAppNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsDryRunVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsPrettyVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsResourceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsYamlVo;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesApiNamespaceArgs;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesResourceVo;
import cn.odboy.common.util.ValidationUtil;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceBuilder;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Kubernetes Namespace Repository
 *
 * @author odboy
 * @date 2024-11-15
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class KubernetesNamespaceRepository {
    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "查询Namespace列表", throwException = false)
    public List<KubernetesResourceVo.Namespace> describeNamespaceList(ApiClient apiClient) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        V1NamespaceList namespaceList = coreV1Api.listNamespace(
                new CustomArgsPrettyVo(false).getValue(),
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
        return namespaceList.getItems().stream().map(m -> {
            KubernetesResourceVo.Namespace namespace = new KubernetesResourceVo.Namespace();
            namespace.setSpec(m.getSpec());
            namespace.setKind(m.getKind());
            namespace.setMetadata(m.getMetadata());
            namespace.setStatus(m.getStatus());
            return namespace;
        }).collect(Collectors.toList());

    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "查询任意一个Namespace", throwException = false)
    public KubernetesResourceVo.Namespace describeNamespace(ApiClient apiClient) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        V1NamespaceList namespaceList = coreV1Api.listNamespace(
                new CustomArgsPrettyVo(false).getValue(),
                true,
                null,
                null,
                null,
                1,
                null,
                null,
                null,
                false
        );
        return namespaceList.getItems().stream().map(m -> {
            KubernetesResourceVo.Namespace namespace = new KubernetesResourceVo.Namespace();
            namespace.setSpec(m.getSpec());
            namespace.setKind(m.getKind());
            namespace.setMetadata(m.getMetadata());
            namespace.setStatus(m.getStatus());
            return namespace;
        }).findAny().orElse(null);
    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据appName查询Namespace", throwException = false)
    public KubernetesResourceVo.Namespace describeNamespaceByAppName(ApiClient apiClient, CustomArgsAppNameVo appNameVo) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        V1Namespace v1Namespace = coreV1Api.readNamespace(
                appNameVo.getValue(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null
        );
        if (v1Namespace == null) {
            return null;
        }
        KubernetesResourceVo.Namespace namespace = new KubernetesResourceVo.Namespace();
        namespace.setSpec(v1Namespace.getSpec());
        namespace.setKind(v1Namespace.getKind());
        namespace.setMetadata(v1Namespace.getMetadata());
        namespace.setStatus(v1Namespace.getStatus());
        return namespace;
    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据name查询Namespace", throwException = false)
    public KubernetesResourceVo.Namespace describeNamespaceByName(ApiClient apiClient, CustomArgsResourceNameVo resourceNameVo) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        V1Namespace v1Namespace = coreV1Api.readNamespace(
                resourceNameVo.getValue(),
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null
        );
        if (v1Namespace == null) {
            return null;
        }
        KubernetesResourceVo.Namespace namespace = new KubernetesResourceVo.Namespace();
        namespace.setSpec(v1Namespace.getSpec());
        namespace.setKind(v1Namespace.getKind());
        namespace.setMetadata(v1Namespace.getMetadata());
        namespace.setStatus(v1Namespace.getStatus());
        return namespace;
    }

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据ApiClient查询任意一个Namespace", throwException = false)
    public KubernetesResourceVo.Namespace describeNamespaceByApiClient(ApiClient apiClient) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        V1NamespaceList namespaceList = coreV1Api.listNamespace(
                new CustomArgsPrettyVo(false).getValue(),
                true,
                null,
                null,
                null,
                1,
                null,
                null,
                null,
                false
        );
        return namespaceList.getItems().stream().map(m -> {
            KubernetesResourceVo.Namespace namespace = new KubernetesResourceVo.Namespace();
            namespace.setSpec(m.getSpec());
            namespace.setKind(m.getKind());
            namespace.setMetadata(m.getMetadata());
            namespace.setStatus(m.getStatus());
            return namespace;
        }).findAny().orElse(null);
    }

    @KubernetesApiExceptionCatch(description = "创建Namespace")
    public KubernetesResourceVo.Namespace createNamespace(ApiClient apiClient, CustomArgsDryRunVo dryRunVo, KubernetesApiNamespaceArgs.Create args) throws Exception {
        ValidationUtil.validate(args);
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        V1Namespace newNamespace = new V1NamespaceBuilder()
                .withApiVersion("v1")
                .withKind("Namespace")
                .withNewMetadata()
                .withName(args.getAppName())
                .endMetadata()
                .build();
        V1Namespace v1Namespace = coreV1Api.createNamespace(
                newNamespace,
                new CustomArgsPrettyVo(false).getValue(),
                dryRunVo.getValue(),
                null
        );
        KubernetesResourceVo.Namespace simpleNamespace = new KubernetesResourceVo.Namespace();
        simpleNamespace.setSpec(v1Namespace.getSpec());
        simpleNamespace.setKind(v1Namespace.getKind());
        simpleNamespace.setMetadata(v1Namespace.getMetadata());
        simpleNamespace.setStatus(v1Namespace.getStatus());
        return simpleNamespace;
    }

    @KubernetesApiExceptionCatch(description = "从Yml加载Namespace")
    public KubernetesResourceVo.Namespace loadNamespaceFromYaml(ApiClient apiClient, CustomArgsDryRunVo dryRunVo, CustomArgsYamlVo<V1Namespace> yamlVo) throws Exception {
        V1Namespace v1Namespace = yamlVo.getTarget();
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        String namespaceName = Objects.requireNonNull(v1Namespace.getMetadata()).getName();
        KubernetesResourceVo.Namespace localNamespace = describeNamespaceByName(apiClient, new CustomArgsResourceNameVo(namespaceName));
        if (localNamespace == null) {
            coreV1Api.createNamespace(
                    v1Namespace,
                    new CustomArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        } else {
            coreV1Api.replaceNamespace(
                    namespaceName,
                    v1Namespace,
                    new CustomArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        }
        KubernetesResourceVo.Namespace simpleNamespace = new KubernetesResourceVo.Namespace();
        simpleNamespace.setSpec(v1Namespace.getSpec());
        simpleNamespace.setKind(v1Namespace.getKind());
        simpleNamespace.setMetadata(v1Namespace.getMetadata());
        simpleNamespace.setStatus(v1Namespace.getStatus());
        return simpleNamespace;
    }

}
