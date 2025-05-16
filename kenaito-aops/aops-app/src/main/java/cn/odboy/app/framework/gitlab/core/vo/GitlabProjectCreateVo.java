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

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class GitlabProjectCreateVo extends GitlabProjectCreateArgs {
    /**
     * OwnerId
     */
    private Long creatorId;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 默认分支
     */
    private String defaultBranch;
    /**
     * git clone 地址
     */
    private String httpUrlToRepo;
    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 可见级别
     */
    private String visibility;
    /**
     * 项目地址
     */
    private String homeUrl;
}
