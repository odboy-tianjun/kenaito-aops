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
package cn.odboy.app.framework.gitlab.core.vo;

import cn.odboy.common.model.MyObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gitlab4j.api.models.Changes;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Repository;
import java.util.List;

/**
 * Gitlab 系统钩子事件回调 发起合并分支请求
 *
 * @author odboy
 * @date 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GitlabSystemHookMergeRequestEventVo extends MyObject {
    /**
     * eg. merge_request
     */
    @JSONField(name = "event_type")
    private String eventType;
    @JSONField(name = "object_attributes")
    private org.gitlab4j.api.webhook.MergeRequestEvent.ObjectAttributes objectAttributes;
    @JSONField(name = "changes")
    private Changes changes;
    @JSONField(name = "project")
    private Project project;
    @JSONField(name = "repository")
    private Repository repository;
    @JSONField(name = "user")
    private org.gitlab4j.api.models.User user;
    @JSONField(name = "object_kind")
    private String objectKind;
    @JSONField(name = "labels")
    private List<String> labels;
}
