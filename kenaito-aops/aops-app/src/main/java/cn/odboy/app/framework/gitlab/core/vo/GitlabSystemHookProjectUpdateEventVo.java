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
import java.util.List;

/**
 * Gitlab 系统钩子事件回调 仓库更新
 *
 * @author odboy
 * @date 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GitlabSystemHookProjectUpdateEventVo extends MyObject {
    /**
     * eg. http://example.com/avatar/user.png
     */
    @JSONField(name = "user_avatar")
    private String userAvatar;
    /**
     * eg. test@example.com
     */
    @JSONField(name = "user_email")
    private String userEmail;
    /**
     * eg. 10
     */
    @JSONField(name = "user_id")
    private Integer userId;
    /**
     * eg. 40
     */
    @JSONField(name = "project_id")
    private Integer projectId;
    /**
     * eg. ["refs/heads/master"]
     */
    @JSONField(name = "refs")
    private List<String> refs;
    /**
     * eg. john.doe
     */
    @JSONField(name = "user_name")
    private String userName;
    /**
     * eg. [{"ref":"refs/heads/master","before":"8205ea8d81ce0c6b90fbe8280d118cc9fdad6130","after":"4045ea7a3df38697b3730a20fb73c8bed8a3e69e"}]
     */
    @JSONField(name = "changes")
    private List<Changes> changes;
    /**
     * repository_update
     */
    @JSONField(name = "event_name")
    private String eventName;
}
