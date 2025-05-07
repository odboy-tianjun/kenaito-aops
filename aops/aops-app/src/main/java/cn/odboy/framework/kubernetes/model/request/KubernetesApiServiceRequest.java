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
package cn.odboy.framework.kubernetes.model.request;

import cn.odboy.base.MyObject;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * K8s Service
 *
 * @author odboy
 * @date 2024-10-01
 */
public class KubernetesApiServiceRequest {
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    public static class Create extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * service注解
         */
        private Map<String, String> annotations;
        /**
         * 外部访问的端口号
         */
        @NotNull(message = "外部访问的端口号不能为空")
        private Integer port;
        /**
         * 应用服务端口号
         */
        @NotNull(message = "应用服务端口号不能为空")
        private Integer targetPort;
        /**
         * pod标签选择器
         */
        private Map<String, String> labelSelector = new HashMap<>();
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Delete extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class LoadFromYaml extends MyObject {
        /**
         * yaml文件内容
         */
        @NotBlank(message = "yaml文件内容不能为空")
        private String yamlContent;
    }
}
