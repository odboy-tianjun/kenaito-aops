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
package cn.odboy.app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用产品线人员角色
 *
 * @author odboy
 * @date 2024-09-13
 */
@Getter
@AllArgsConstructor
public enum AppProductLineUserRoleEnum {
    ADMIN("owner", "负责人"),
    PE("pe", "PE");

    private final String code;
    private final String desc;

    public static AppProductLineUserRoleEnum getByCode(String code) {
        for (AppProductLineUserRoleEnum item : AppProductLineUserRoleEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
