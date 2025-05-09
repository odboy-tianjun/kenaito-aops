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
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * K8s StatefulSet
 *
 * @author odboy
 * @date 2024-10-01
 */
public class KubernetesApiStatefulSetRequest {
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Create extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
//        /**
//         * statefulset注解
//         */
//        private Map<String, String> annotations;
        /**
         * 副本数量
         */
        @NotNull(message = "副本数量不能为空")
        @Min(value = 0, message = "副本数量不能小于0")
        private Integer replicas;
        /**
         * 镜像地址
         */
        @NotBlank(message = "镜像地址不能为空")
        private String image;
        /**
         * 容器服务端口号
         */
        @NotNull(message = "容器服务端口号不能为空")
        private Integer port;
        /**
         * 需求cpu数
         */
        @NotNull(message = "需求cpu数不能为空")
        @Min(value = 1, message = "需求cpu数不能小于1")
        private Integer requestCpuNum;
        /**
         * 需求memory数
         */
        @NotNull(message = "需求memory数不能为空")
        @Min(value = 1, message = "需求memory数不能小于1")
        private Integer requestMemNum;
        /**
         * cpu上限
         */
        @NotNull(message = "cpu上限不能为空")
        private Integer limitsCpuNum;
        /**
         * memory上限
         */
        @NotNull(message = "memory上限不能为空")
        private Integer limitsMemNum;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ChangeReplicas extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * 新副本数量
         */
        @NotNull(message = "新副本数量不能为空")
        @Min(value = 0, message = "新副本数量不能小于0")
        private Integer newReplicas;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ChangeImage extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * 新镜像地址
         */
        @NotBlank(message = "镜像地址不能为空")
        private String newImage;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ChangeSpecs extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * 需求cpu数
         */
        @NotNull(message = "需求cpu数不能为空")
        @Min(value = 1, message = "需求cpu数不能小于1")
        private Integer requestCpuNum;
        /**
         * 需求memory数
         */
        @NotNull(message = "需求memory数不能为空")
        @Min(value = 1, message = "需求memory数不能小于1")
        private Integer requestMemNum;
        /**
         * cpu上限
         */
        @NotNull(message = "cpu上限不能为空")
        private Integer limitsCpuNum;
        /**
         * memory上限
         */
        @NotNull(message = "memory上限不能为空")
        private Integer limitsMemNum;
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
}
