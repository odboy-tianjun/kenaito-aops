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
package cn.odboy.constant;

/**
 * 常用正则表达式
 *
 * @author odboy
 * @date 2024-09-13
 */
public interface GlobalRegexConst {
    /**
     * 用于匹配中国大陆手机号码
     */
    String PHONE_NUMBER = "^1[3-9]\\d{9}$";
    /**
     * 小写字母和下划线
     */
    String LOW_CHAR_LINE = "^[a-z_]+$";
}
