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
 * 是否格式化输出
 */
@Value
public class ArgsPrettyVo {
    Boolean flag;

    public String getValue() {
        if (this.flag == null) {
            return "false";
        }
        return this.flag ? "true" : "false";
    }
}
