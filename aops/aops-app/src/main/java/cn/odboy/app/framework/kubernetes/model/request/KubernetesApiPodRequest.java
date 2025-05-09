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
import javax.validation.constraints.NotBlank;

/**
 * K8s Pod
 *
 * @author odboy
 * @date 2024-10-01
 */
public class KubernetesApiPodRequest {
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Rebuild extends MyObject {
        /**
         * 命名空间
         */
        @NotBlank(message = "命名空间不能为空")
        private String namespace;
        /**
         * pod名称
         */
        @NotBlank(message = "pod名称不能为空")
        private String podName;
    }
}
