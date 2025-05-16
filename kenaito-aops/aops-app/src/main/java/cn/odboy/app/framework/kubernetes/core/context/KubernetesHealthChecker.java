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
package cn.odboy.app.framework.kubernetes.core.context;

import cn.odboy.app.framework.kubernetes.core.repository.KubernetesNamespaceRepository;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsClusterCodeVo;
import cn.odboy.app.framework.kubernetes.core.vo.KubernetesResourceVo;
import cn.odboy.common.exception.BadRequestException;
import io.kubernetes.client.openapi.ApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * k8s健康检查，检查配置文件内容
 *
 * @author odboy
 * @date 2025-01-13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KubernetesHealthChecker {
    private final KubernetesNamespaceRepository kubernetesNamespaceRepository;
    private final KubernetesApiClientManager kubernetesApiClientManager;

    public void checkConfigContent(CustomArgsClusterCodeVo clusterCodeVo) {
        try {
            ApiClient apiClient = kubernetesApiClientManager.getClient(clusterCodeVo.getValue());
            KubernetesResourceVo.Namespace namespace = kubernetesNamespaceRepository.describeNamespace(apiClient);
            if (namespace == null) {
                throw new BadRequestException("无效配置");
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void checkConfigContent(ApiClient apiClient) {
        try {
            KubernetesResourceVo.Namespace namespace = kubernetesNamespaceRepository.describeNamespaceByApiClient(apiClient);
            if (namespace == null) {
                throw new BadRequestException("无效配置");
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
