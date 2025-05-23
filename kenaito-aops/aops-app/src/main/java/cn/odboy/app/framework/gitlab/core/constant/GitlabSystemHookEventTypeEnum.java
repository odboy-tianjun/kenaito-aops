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
package cn.odboy.app.framework.gitlab.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统钩子类型
 *
 * @author odboy
 * @date 2025-01-17
 */
@Getter
@AllArgsConstructor
public enum GitlabSystemHookEventTypeEnum {
    RepositoryUpdate("repository_update", "仓库更新"),
    Push("push", "提交推送"),
    MergeRequest("merge_request", "合并请求");
    private final String code;
    private final String desc;

    public static GitlabSystemHookEventTypeEnum getByCode(String code) {
        for (GitlabSystemHookEventTypeEnum item : GitlabSystemHookEventTypeEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
