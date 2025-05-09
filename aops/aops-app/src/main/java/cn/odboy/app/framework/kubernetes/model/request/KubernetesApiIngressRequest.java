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
package cn.odboy.app.framework.kubernetes.model.request;

import cn.odboy.common.pojo.MyObject;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * K8s Ingress
 *
 * @author odboy
 * @date 2024-10-01
 */
public class KubernetesApiIngressRequest {
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = false)
    public static class Create extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
//        /**
//         * ingress注解
//         */
//        private Map<String, String> annotations;
        /**
         * 匹配的路径
         */
        @NotBlank(message = "匹配的路径不能为空")
        private String path;
        /**
         * 绑定的域名
         */
        @NotBlank(message = "绑定的域名不能为空")
        private String hostname;
        /**
         * 路由到的服务名称
         */
        @NotBlank(message = "路由到的服务名称不能为空")
        private String serviceName;
        /**
         * 路由到的服务端口
         */
        @NotNull(message = "路由到的服务端口不能为空")
        private Integer servicePort;
    }

    @Data
    @Builder
    @EqualsAndHashCode(callSuper = false)
    public static class Delete extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
    }
}
