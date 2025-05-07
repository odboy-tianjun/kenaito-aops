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

import cn.hutool.core.lang.Assert;
import cn.odboy.exception.BadRequestException;
import cn.odboy.framework.kubernetes.constant.KubernetesActionReasonCodeEnum;
import cn.odboy.framework.kubernetes.context.KubernetesApiClientManager;
import cn.odboy.framework.kubernetes.model.request.KubernetesApiStatefulSetRequest;
import cn.odboy.framework.kubernetes.model.response.KubernetesApiExceptionResponse;
import cn.odboy.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsDryRunVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsPrettyVo;
import cn.odboy.util.ValidationUtil;
import com.alibaba.fastjson2.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.util.Yaml;
import io.openkruise.client.models.KruiseAppsV1alpha1StatefulSet;
import lombok.RequiredArgsConstructor;
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
    private final KubernetesApiClientManager k8SClientAdmin;
    private final KubernetesPodRepository kubernetesPodRepository;

    /**
     * 根据name获取KruiseStatefulSet
     *
     * @return /
     */
    public KruiseAppsV1alpha1StatefulSet describeStatefulSetByName(String clusterCode, String name, String namespace) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(name, "名称不能为空");
        Assert.notEmpty(namespace, "命名空间不能为空");
        try {
            CustomObjectsApi customObjectsApi = new CustomObjectsApi(k8SClientAdmin.getClient(clusterCode));
            Object obj = customObjectsApi.getNamespacedCustomObject(
                    "apps.kruise.io",
                    "v1beta1",
                    namespace,
                    "statefulsets",
                    name
            );
            return JSON.parseObject(JSON.toJSONString(obj), KruiseAppsV1alpha1StatefulSet.class);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(KubernetesActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据name获取KruiseStatefulSet失败: {}", responseBody, e);
                throw new BadRequestException("根据name获取KruiseStatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据name获取KruiseStatefulSet失败");
        } catch (Exception e) {
            log.error("根据name获取KruiseStatefulSet失败:", e);
            throw new BadRequestException("根据name获取KruiseStatefulSet失败");
        }
    }

    /**
     * 从yaml文件内容加载KruiseStatefulSet
     *
     * @param args /
     */
    public KruiseAppsV1alpha1StatefulSet loadStatefulSetFromYaml(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesApiStatefulSetRequest.LoadFromYaml args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCodeVo.getValue());
            CustomObjectsApi customObjectsApi = new CustomObjectsApi(apiClient);
            KruiseAppsV1alpha1StatefulSet statefulSet = Yaml.loadAs(args.getYamlContent(), KruiseAppsV1alpha1StatefulSet.class);
            String statefulSetName = Objects.requireNonNull(statefulSet.getMetadata()).getName();
            String namespace = Objects.requireNonNull(statefulSet.getMetadata()).getNamespace();
            KruiseAppsV1alpha1StatefulSet statefulSetByName = describeStatefulSetByName(clusterCodeVo.getValue(), statefulSetName, namespace);
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
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载KruiseStatefulSet失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载KruiseStatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载KruiseStatefulSet失败");
        } catch (Exception e) {
            log.error("从yml加载KruiseStatefulSet失败:", e);
            throw new BadRequestException("从yml加载KruiseStatefulSet失败");
        }
    }
}
