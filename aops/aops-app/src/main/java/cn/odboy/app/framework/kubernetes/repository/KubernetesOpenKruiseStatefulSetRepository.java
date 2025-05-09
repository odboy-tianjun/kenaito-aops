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
package cn.odboy.app.framework.kubernetes.repository;

import cn.odboy.app.framework.kubernetes.context.KubernetesApiClientManager;
import cn.odboy.app.framework.kubernetes.exception.KubernetesApiExceptionCatch;
import cn.odboy.app.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.app.framework.kubernetes.model.vo.ArgsDryRunVo;
import cn.odboy.app.framework.kubernetes.model.vo.ArgsNamespaceNameVo;
import cn.odboy.app.framework.kubernetes.model.vo.ArgsPrettyVo;
import cn.odboy.app.framework.kubernetes.model.vo.ArgsResourceNameVo;
import cn.odboy.app.framework.kubernetes.model.vo.ArgsYamlVo;
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
    private final KubernetesApiClientManager kubernetesApiClientManager;

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "根据name和namespace查询KruiseStatefulSet", throwException = false)
    public KruiseAppsV1alpha1StatefulSet describeStatefulSetByName(ArgsClusterCodeVo clusterCodeVo, ArgsResourceNameVo resourceNameVo, ArgsNamespaceNameVo namespaceNameVo) {
        CustomObjectsApi customObjectsApi = new CustomObjectsApi(kubernetesApiClientManager.getClient(clusterCodeVo.getValue()));
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
    public KruiseAppsV1alpha1StatefulSet loadStatefulSetFromYaml(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, ArgsYamlVo<KruiseAppsV1alpha1StatefulSet> yamlVo) throws Exception {
        ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
        CustomObjectsApi customObjectsApi = new CustomObjectsApi(apiClient);
        KruiseAppsV1alpha1StatefulSet statefulSet = yamlVo.getTarget();
        String statefulSetName = Objects.requireNonNull(statefulSet.getMetadata()).getName();
        String namespace = Objects.requireNonNull(statefulSet.getMetadata()).getNamespace();
        KruiseAppsV1alpha1StatefulSet statefulSetByName = describeStatefulSetByName(
                clusterCodeVo,
                new ArgsResourceNameVo(statefulSetName),
                new ArgsNamespaceNameVo(namespace)
        );
        if (statefulSetByName == null) {
            customObjectsApi.createNamespacedCustomObject(
                    "apps.kruise.io",
                    "v1beta1",
                    namespace,
                    "statefulsets",
                    statefulSet,
                    new ArgsPrettyVo(false).getValue(),
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
