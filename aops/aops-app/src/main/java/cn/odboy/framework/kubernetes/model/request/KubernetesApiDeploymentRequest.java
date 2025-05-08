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
package cn.odboy.framework.kubernetes.model.request;

import cn.odboy.base.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用途: 主要用于无状态应用程序的部署和管理，例如 web 服务器、API 服务等。
 * 应用场景: 适用于需要快速扩展、无状态的服务，即使 Pod 在不同的节点上重启或被重新调度，也不会影响应用的正常运行。
 *
 * @author odboy
 * @date 2025-01-13
 */
public class KubernetesApiDeploymentRequest {
    /**
     * 创建Deployment
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Create extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
//        /**
//         * deployment注解
//         */
//        private Map<String, String> annotations;
        /**
         * 镜像地址
         */
        @NotBlank(message = "镜像地址不能为空")
        private String image;
        /**
         * 副本数量
         */
        @NotNull(message = "副本数量不能为空")
        @Min(value = 0, message = "副本数量不能小于0")
        private Integer replicas;
        /**
         * 容器服务端口号
         */
        @NotNull(message = "容器服务端口号不能为空")
        private Integer port;
    }

    /**
     * 修改Deployment副本数量
     */
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

    /**
     * 修改Deployment镜像地址
     */
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
        @NotBlank(message = "新镜像地址不能为空")
        private String newImage;
    }

    /**
     * 修改Deployment规格
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ChangePodSpecs extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * CPU数
         */
        @NotNull
        @Min(value = 1, message = "CPU数不能小于0")
        private Integer cpuNum;
        /**
         * 内存数
         */
        @NotNull
        @Min(value = 1, message = "内存数不能小于0")
        private Integer memoryNum;
    }

    /**
     * 删除Deployment
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Delete extends MyObject {
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
    }

    /**
     * 从yaml文件加载Deployment
     */
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
