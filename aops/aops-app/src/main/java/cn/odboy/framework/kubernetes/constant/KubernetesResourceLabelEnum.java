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
package cn.odboy.framework.kubernetes.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Kubernetes 资源标签
 *
 * @author odboy
 * @date 2025-05-07
 */
@Getter
@AllArgsConstructor
public enum KubernetesResourceLabelEnum {
    AppName("appName", "应用名称"),
    AppEnv("appEnv", "环境");

    private final String code;
    private final String desc;

    public static KubernetesResourceLabelEnum getByCode(String code) {
        for (KubernetesResourceLabelEnum item : KubernetesResourceLabelEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
