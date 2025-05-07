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
package cn.odboy.framework.kubernetes.model.vo;

import lombok.Value;

/**
 * 选项值	说明
 * "All"	执行所有阶段的验证（包括准入控制、Schema 校验等），但不持久化到存储。
 * null	默认值，不启用 Dry Run，直接执行请求。
 * 其他字符串	通常无效，会被 API Server 忽略（行为等同于 null）。
 */
@Value
public class ArgsDryRunVo {
    Boolean flag;

    public String getValue() {
        if (this.flag == null) {
            return null;
        }
        return this.flag ? "All" : null;
    }
}
