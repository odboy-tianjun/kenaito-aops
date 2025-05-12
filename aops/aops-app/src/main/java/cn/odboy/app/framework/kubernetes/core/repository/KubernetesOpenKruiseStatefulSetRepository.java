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
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsDryRunVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsNamespaceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsPrettyVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsResourceNameVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsYamlVo;
import com.alibaba.fastjson2.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.openkruise.client.models.KruiseAppsV1alpha1StatefulSet;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * Kubernetes StatefulSet Repository For OpenKruise
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KubernetesOpenKruiseStatefulSetRepository {
    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据name和namespace查询KruiseStatefulSet", throwException = false)
    public KruiseAppsV1alpha1StatefulSet describeStatefulSetByName(ApiClient apiClient, CustomArgsResourceNameVo resourceNameVo, CustomArgsNamespaceNameVo namespaceNameVo) {
        CustomObjectsApi customObjectsApi = new CustomObjectsApi(apiClient);
        Object obj = customObjectsApi.getNamespacedCustomObject(
                "apps.kruise.io",
                "v1beta1",
                namespaceNameVo.getValue(),
                "statefulsets",
                resourceNameVo.getValue()
        );
        return JSON.parseObject(JSON.toJSONString(obj), KruiseAppsV1alpha1StatefulSet.class);
    }

    @KubernetesApiExceptionCatch(description = "从yml加载KruiseStatefulSet")
    public KruiseAppsV1alpha1StatefulSet loadStatefulSetFromYaml(ApiClient apiClient, CustomArgsDryRunVo dryRunVo, CustomArgsYamlVo<KruiseAppsV1alpha1StatefulSet> yamlVo) throws Exception {
        CustomObjectsApi customObjectsApi = new CustomObjectsApi(apiClient);
        KruiseAppsV1alpha1StatefulSet statefulSet = yamlVo.getTarget();
        String statefulSetName = Objects.requireNonNull(statefulSet.getMetadata()).getName();
        String namespace = Objects.requireNonNull(statefulSet.getMetadata()).getNamespace();
        KruiseAppsV1alpha1StatefulSet statefulSetByName = describeStatefulSetByName(
                apiClient,
                new CustomArgsResourceNameVo(statefulSetName),
                new CustomArgsNamespaceNameVo(namespace)
        );
        if (statefulSetByName == null) {
            customObjectsApi.createNamespacedCustomObject(
                    "apps.kruise.io",
                    "v1beta1",
                    namespace,
                    "statefulsets",
                    statefulSet,
                    new CustomArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(),
                    null
            );
        } else {
            customObjectsApi.replaceClusterCustomObject(
                    "apps.kruise.io",
                    "v1beta1",
                    "statefulsets",
                    statefulSetName,
                    statefulSet,
                    dryRunVo.getValue(),
                    null
            );
        }
        return statefulSet;
    }
}
