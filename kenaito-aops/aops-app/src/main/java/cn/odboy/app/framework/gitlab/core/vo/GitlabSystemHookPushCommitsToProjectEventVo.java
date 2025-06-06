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
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import java.util.List;

/**
 * Gitlab 系统钩子事件回调 推送提交到项目
 *
 * @author odboy
 * @date 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GitlabSystemHookPushCommitsToProjectEventVo extends MyObject {
    /**
     * eg. https://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=8://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=80
     */
    @JSONField(name = "user_avatar")
    private String userAvatar;
    /**
     * eg. john@example.com
     */
    @JSONField(name = "user_email")
    private String userEmail;
    /**
     * eg. 1
     */
    @JSONField(name = "total_commits_count")
    private Integer totalCommitsCount;
    /**
     * eg. 95790bf891e76fee5e1747ab589903a6a1f80f22
     */
    @JSONField(name = "before")
    private String before;
    /**
     * eg. John Smith
     */
    @JSONField(name = "user_name")
    private String userName;
    /**
     * eg. {"ci":{"skip":true}}
     */
    @JSONField(name = "push_options")
    private JSONObject pushOptions;
    /**
     * eg. da1560886d4f094c3e6c9ef40349f7d38b5d27d7
     */
    @JSONField(name = "checkout_sha")
    private String checkoutSha;
    /**
     * {
     * "web_url": "http://test.example.com/gitlab/gitlab",
     * "avatar_url": "https://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=8://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=80",
     * "path_with_namespace": "gitlab/gitlab",
     * "name": "gitlab",
     * "namespace": "gitlab",
     * "description": "",
     * "visibility_level": 0,
     * "default_branch": "master",
     * "id": 15,
     * "git_http_url": "http://test.example.com/gitlab/gitlab.git",
     * "git_ssh_url": "git@test.example.com:gitlab/gitlab.git"
     * }
     */
    @JSONField(name = "project")
    private Project project;
    /**
     * eg. true
     */
    @JSONField(name = "ref_protected")
    private boolean refProtected;
    /**
     * eg. Hello World
     */
    @JSONField(name = "message")
    private String message;
    /**
     * eg. push
     */
    @JSONField(name = "object_kind")
    private String objectKind;
    /**
     * eg. refs/heads/master
     */
    @JSONField(name = "ref")
    private String ref;
    /**
     * eg. 4
     */
    @JSONField(name = "user_id")
    private int userId;
    /**
     * eg. 15
     */
    @JSONField(name = "project_id")
    private int projectId;
    /**
     * eg. push
     */
    @JSONField(name = "event_name")
    private String eventName;
    /**
     * {
     * "author": {
     * "name": "Test UserDO",
     * "email": "test@example.com"
     * },
     * "id": "c5feabde2d8cd023215af4d2ceeb7a64839fc428",
     * "message": "Add simple search to projects in public area\n\ncommit message body",
     * "title": "Add simple search to projects in public area",
     * "url": "https://test.example.com/gitlab/gitlab/-/commit/c5feabde2d8cd023215af4d2ceeb7a64839fc428",
     * "timestamp": "2013-05-13T18:18:08+00:00"    * 	}
     */
    @JSONField(name = "commits")
    private List<Commit> commits;
    /**
     * eg. da1560886d4f094c3e6c9ef40349f7d38b5d27d7
     */
    @JSONField(name = "after")
    private String after;
}
