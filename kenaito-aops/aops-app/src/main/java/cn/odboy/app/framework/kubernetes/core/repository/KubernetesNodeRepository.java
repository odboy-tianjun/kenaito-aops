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
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsPrettyVo;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeList;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * K8s Node
 *
 * @author odboy
 * @date 2024-10-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KubernetesNodeRepository {

    @SneakyThrows
    @KubernetesApiExceptionCatch(description = "查询Node节点列表", throwException = false)
    public V1NodeList describeNodeList(ApiClient apiClient) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        return coreV1Api.listNode(
                new CustomArgsPrettyVo(false).getValue(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
