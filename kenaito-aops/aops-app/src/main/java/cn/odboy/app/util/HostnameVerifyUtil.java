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
package cn.odboy.app.util;

/**
 * 主机名校验工具
 *
 * @author odboy
 * @date 2025-05-07
 */
public class HostnameVerifyUtil {
    private static final String DOMAIN_PATTERN = "^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$";

    public static boolean isDomain(String domain) {
        if (domain == null || domain.isEmpty()) {
            return false;
        }
        return domain.matches(DOMAIN_PATTERN);
    }
}
