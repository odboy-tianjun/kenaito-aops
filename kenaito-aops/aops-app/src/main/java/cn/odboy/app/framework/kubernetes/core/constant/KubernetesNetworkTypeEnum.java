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
package cn.odboy.app.framework.kubernetes.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Kubernetes 网络类型
 *
 * @author odboy
 * @date 2024-11-21
 */
@Getter
@AllArgsConstructor
public enum KubernetesNetworkTypeEnum {
    INNER("inner", ".com", "内网"),
    OUTER("outer", ".cn", "外网");

    private final String code;
    private final String suffix;
    private final String desc;

    public static KubernetesNetworkTypeEnum getByCode(String code) {
        for (KubernetesNetworkTypeEnum item : KubernetesNetworkTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
