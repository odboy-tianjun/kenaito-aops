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
import cn.odboy.framework.kubernetes.model.args.KubernetesIngressApiArgs;
import cn.odboy.framework.kubernetes.model.response.KubernetesApiExceptionResponse;
import cn.odboy.framework.kubernetes.model.vo.ArgsClusterCodeVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsDryRunVo;
import cn.odboy.framework.kubernetes.model.vo.ArgsPrettyVo;
import cn.odboy.framework.kubernetes.util.KubernetesResourceNameUtil;
import cn.odboy.util.ValidationUtil;
import com.alibaba.fastjson2.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.V1HTTPIngressPathBuilder;
import io.kubernetes.client.openapi.models.V1HTTPIngressRuleValueBuilder;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1IngressBackendBuilder;
import io.kubernetes.client.openapi.models.V1IngressBuilder;
import io.kubernetes.client.openapi.models.V1IngressServiceBackendBuilder;
import io.kubernetes.client.openapi.models.V1ServiceBackendPortBuilder;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
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

    /**
     * 创建k8s V1Ingress
     *
     * @param args /
     */
    public V1Ingress createIngress(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesIngressApiArgs.Create args) {
        ValidationUtil.validate(args);
        try {
            // 构建ingress的yaml对象
            V1Ingress ingress = new V1IngressBuilder()
                    .withNewMetadata()
                    .withName(KubernetesResourceNameUtil.getIngressName(args.getAppName(), kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue())))
                    .withNewNamespace(args.getAppName())
                    .withAnnotations(args.getAnnotations())
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
            return networkingV1Api.createNamespacedIngress(args.getAppName(), ingress, new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建Ingress失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建Ingress失败");
        } catch (Exception e) {
            log.error("创建Ingress失败:", e);
            throw new BadRequestException("创建Ingress失败");
        }
    }

    /**
     * 删除Ingress
     *
     * @param args /
     */
    public void deleteIngress(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesIngressApiArgs.Delete args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            networkingV1Api.deleteNamespacedIngress(KubernetesResourceNameUtil.getIngressName(args.getAppName(),
                            kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue())),
                    args.getAppName(), new ArgsPrettyVo(false).getValue(),
                    dryRunVo.getValue(), null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("删除Ingress失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("删除Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("删除Ingress失败");
        } catch (Exception e) {
            log.error("删除Ingress失败:", e);
            throw new BadRequestException("删除Ingress失败");
        }
    }

    /**
     * 根据appName获取Ingress
     *
     * @return /
     */
    public V1Ingress describeIngressByAppName(ArgsClusterCodeVo clusterCodeVo, String appName) {
        Assert.notNull(clusterCodeVo, "集群编码不能为空");
        Assert.notEmpty(appName, "应用名称不能为空");
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            return networkingV1Api.readNamespacedIngress(KubernetesResourceNameUtil.getIngressName(appName, kubernetesApiClientManager.getEnvCode(clusterCodeVo.getValue())), appName, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(KubernetesActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据appName获取Ingress失败: {}", responseBody, e);
                throw new BadRequestException("根据appName获取Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据appName获取Ingress失败");
        } catch (Exception e) {
            log.error("根据appName获取Ingress失败:", e);
            throw new BadRequestException("根据appName获取Ingress失败");
        }
    }

    /**
     * 根据ingress名称获取Ingress
     *
     * @param name      ingress名称
     * @param namespace 命名空间
     * @return /
     */
    public V1Ingress describeIngressByName(ArgsClusterCodeVo clusterCodeVo, String name, String namespace) {
        Assert.notNull(clusterCodeVo, "集群编码不能为空");
        Assert.notEmpty(name, "名称不能为空");
        Assert.notEmpty(namespace, "命名空间不能为空");
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            return networkingV1Api.readNamespacedIngress(name, namespace, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(KubernetesActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据ingress名称获取Ingress失败: {}", responseBody, e);
                throw new BadRequestException("根据ingress名称获取Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据ingress名称获取Ingress失败");
        } catch (Exception e) {
            log.error("根据ingress名称获取Ingress失败:", e);
            throw new BadRequestException("根据ingress名称获取Ingress失败");
        }
    }

    /**
     * 从yaml文件内容加载Ingress
     *
     * @param args /
     */
    public V1Ingress loadIngressFromYaml(ArgsClusterCodeVo clusterCodeVo, ArgsDryRunVo dryRunVo, KubernetesIngressApiArgs.LoadFromYaml args) {
        ValidationUtil.validate(args);
        try {
            V1Ingress ingress = Yaml.loadAs(args.getYamlContent(), V1Ingress.class);
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            String ingressName = Objects.requireNonNull(ingress.getMetadata()).getName();
            String namespace = Objects.requireNonNull(ingress.getMetadata()).getNamespace();
            V1Ingress v1Ingress = describeIngressByName(clusterCodeVo, ingressName, namespace);
            if (v1Ingress == null) {
                networkingV1Api.createNamespacedIngress(namespace, ingress, new ArgsPrettyVo(false).getValue(),
                        dryRunVo.getValue(), null);
            } else {
                networkingV1Api.replaceNamespacedIngress(ingressName, namespace, ingress, new ArgsPrettyVo(false).getValue(),
                        dryRunVo.getValue(), null);
            }
            return ingress;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载Ingress失败: {}", responseBody, e);
            KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载Ingress失败");
        } catch (Exception e) {
            log.error("从yml加载Ingress失败:", e);
            throw new BadRequestException("从yml加载Ingress失败");
        }
    }
}
