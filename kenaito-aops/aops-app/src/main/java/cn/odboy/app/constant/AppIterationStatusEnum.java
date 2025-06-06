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
 * 应用迭代状态
 *
 * @author odboy
 * @date 2024-11-15
 */
@Getter
@AllArgsConstructor
public enum AppIterationStatusEnum {
    READY(1, "未开始"),
    LOADING(2, "进行中"),
    FINISH(3, "已完成");

    private final Integer code;
    private final String desc;

    public static AppIterationStatusEnum getByCode(Integer code) {
        for (AppIterationStatusEnum item : AppIterationStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getDescByCode(Integer code) {
        for (AppIterationStatusEnum item : AppIterationStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item.getDesc();
            }
        }
        return "";
    }
}
