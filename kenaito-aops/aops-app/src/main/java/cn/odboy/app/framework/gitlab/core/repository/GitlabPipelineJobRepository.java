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
package cn.odboy.app.framework.gitlab.core.repository;

import cn.odboy.app.framework.gitlab.core.context.GitlabApiClientManager;
import cn.odboy.app.framework.gitlab.core.exception.GitlabApiExceptionCatch;
import cn.odboy.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.Project;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Gitlab流水线任务
 *
 * @author odboy
 * @date 2024-11-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabPipelineJobRepository {
    private final GitlabApiClientManager gitlabApiClientManager;
    private final GitlabProjectRepository gitlabProjectRepository;

    /**
     * @param projectId  项目id
     * @param pipelineId 流水线id
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId和pipelineId查询任务列表", throwException = false)
    public List<Job> describeJobListByProjectIdWithPipelineId(Long projectId, Long pipelineId) {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getJobApi().getJobsForPipeline(projectId, pipelineId);
        }
    }

    /**
     * @param projectName 项目名称
     * @param pipelineId  流水线id
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectName和pipelineId查询任务列表", throwException = false)
    public List<Job> describeJobListByProjectNameWithPipelineId(String projectName, Long pipelineId) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describeJobListByProjectIdWithPipelineId(project.getId(), pipelineId);
    }

    /**
     * @param projectId 项目id
     * @param jobId     任务id
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId和jobId查询任务详情", throwException = false)
    public Job describeJobByProjectIdWithJobId(Long projectId, Long jobId) {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getJobApi().getJob(projectId, jobId);
        }
    }

    /**
     * @param projectName 项目名称
     * @param jobId       任务id
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectName和jobId查询任务详情", throwException = false)
    public Job describeJobByProjectNameWithJobId(String projectName, Long jobId) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describeJobByProjectIdWithJobId(project.getId(), jobId);
    }
}
