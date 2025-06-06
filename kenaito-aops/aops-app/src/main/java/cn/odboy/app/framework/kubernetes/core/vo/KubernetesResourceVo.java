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
package cn.odboy.app.framework.kubernetes.core.vo;

import io.kubernetes.client.openapi.models.V1NamespaceSpec;
import io.kubernetes.client.openapi.models.V1NamespaceStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodCondition;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * K8s 资源响应信息
 *
 * @author odboy
 * @date 2024-10-01
 */
public class KubernetesResourceVo {

    @Data
    public static class Namespace {
        private V1NamespaceSpec spec;
        private String kind;
        private V1ObjectMeta metadata;
        private V1NamespaceStatus status;
    }

    @Data
    public static class Pod {
        private String name;
        private String ip;
        private Date createTime;
        private Date deleteTime;
        private Date startTime;
        private Map<String, String> labels;
        private String namespace;
        private String resourceVersion;
        private String restartPolicy;
        private Integer restartCount;
        private String image;
        private String status;
        private String qosClass;
        /**
         * pod 流转状态
         */
        private List<V1PodCondition> conditions;
    }
}
