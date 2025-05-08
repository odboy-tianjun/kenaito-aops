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
 * Kubernetes容器规格
 *
 * @author odboy
 * @date 2022-11-01
 */
@Getter
@AllArgsConstructor
public enum KubernetesContainerSpecsEnum {
    KCS1C1G("1核1G", "1c", "1g"),
    KCS1C2G("1核2G", "1c", "2g"),
    KCS1C4G("1核4G", "1c", "4g"),
    KCS2C4G("2核4G", "2c", "4g"),
    KCS2C8G("2核8G", "2c", "8g"),
    KCS4C4G("4核4G", "4c", "4g"),
    KCS4C8G("4核8G", "4c", "8g");
    private final String title;
    private final String cpu;
    private final String mem;
}
