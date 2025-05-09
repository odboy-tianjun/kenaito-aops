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
package cn.odboy.app.framework.kubernetes.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Kubernetes 网络后缀
 *
 * @author odboy
 * @date 2024-11-21
 */
@Getter
@AllArgsConstructor
public enum KubernetesNetworkSuffixEnum {
    INNER(".com", "内网"),
    OUTER(".cn", "外网");

    private final String code;
    private final String desc;

    public static KubernetesNetworkSuffixEnum getByCode(String code) {
        for (KubernetesNetworkSuffixEnum item : KubernetesNetworkSuffixEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
