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
package cn.odboy.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否启用
 *
 * @author odboy
 * @date 2025-05-15
 */
@Getter
@AllArgsConstructor
public enum GlobalEnableStatusEnum {
    ENABLED(1L, "启用", "success"),
    DISABLED(0L, "禁用", "danger");
    private final Long code;
    private final String desc;
    private final String tagType;

    public static GlobalEnableStatusEnum getByCode(Integer code) {
        for (GlobalEnableStatusEnum item : GlobalEnableStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
