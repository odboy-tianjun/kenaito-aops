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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gitlab4j.api.models.JobStatus;
import java.util.Date;

/**
 * 流水线任务信息
 *
 * @author odboy
 * @date 2025-01-17
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class GitlabPipelineJobInfoVo extends MyObject {
    private Long id;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 启用时间
     */
    private Date startedAt;
    /**
     * 执行耗时，单位秒
     */
    private Float duration;
    /**
     * 任务状态
     */
    private JobStatus status;
    /**
     * 任务执行过程url
     */
    private String webUrl;
}
