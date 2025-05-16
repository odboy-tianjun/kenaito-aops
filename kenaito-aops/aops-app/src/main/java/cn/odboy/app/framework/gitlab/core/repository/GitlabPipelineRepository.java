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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.odboy.app.framework.gitlab.core.callback.GitlabPipelineJobExecuteCallback;
import cn.odboy.app.framework.gitlab.core.context.GitlabApiClientManager;
import cn.odboy.app.framework.gitlab.core.exception.GitlabApiExceptionCatch;
import cn.odboy.app.framework.gitlab.core.vo.GitlabPipelineJobInfoVo;
import cn.odboy.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.JobStatus;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.PipelineStatus;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * Gitlab流水线
 *
 * @author odboy
 * @date 2024-11-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabPipelineRepository {
    @Autowired
    private GitlabApiClientManager gitlabApiClientManager;
    @Autowired
    private GitlabProjectRepository gitlabProjectRepository;
    @Autowired
    private GitlabPipelineJobRepository gitlabPipelineJobRepository;

    /**
     * @param projectId  项目id
     * @param pipelineId 流水线id
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId和pipelineId查询流水线详情", throwException = false)
    public Pipeline describePipelineByProjectIdWithPipelineId(Long projectId, Long pipelineId) {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getPipelineApi().getPipeline(projectId, pipelineId);
        }
    }

    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectName和pipelineId查询流水线详情", throwException = false)
    public Pipeline describePipelineByProjectNameWithPipelineId(String projectName, Long pipelineId) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describePipelineByProjectIdWithPipelineId(project.getId(), pipelineId);
    }

    /**
     * @param projectId 项目id
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId查询流水线列表", throwException = false)
    public List<Pipeline> describePipelineListByProjectId(Long projectId) {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getPipelineApi().getPipelines(projectId);
        }
    }

    /**
     * @param projectName 项目名称
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectName查询流水线列表", throwException = false)
    public List<Pipeline> describePipelineListByProjectName(String projectName) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describePipelineListByProjectId(project.getId());
    }

    /**
     * @param projectId 项目id
     * @param ref       分支名称
     * @param variables 流水线变量，可在.gitlab-ci.yml文件中通过语法 $变量名称，获取变量值
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectId和ref创建流水线")
    public Pipeline createPipelineByProjectIdWithRef(Long projectId, String ref, Map<String, String> variables) throws Exception {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            return client.getPipelineApi().createPipeline(projectId, ref, variables);
        }
    }

    /**
     * @param projectName 项目名称
     * @param ref         分支名称
     * @param variables   流水线变量，可在.gitlab-ci.yml文件中通过语法 $变量名称，获取变量值
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectName和ref创建流水线")
    public Pipeline createPipelineByProjectNameWithRef(String projectName, String ref, Map<String, String> variables) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return createPipelineByProjectIdWithRef(project.getId(), ref, variables);
    }


    /**
     * @param projectId  项目id
     * @param pipelineId 流水线id
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectId和pipelineId停止流水线")
    public Pipeline stopPipelineByProjectIdWithPipelineId(Long projectId, Long pipelineId) throws Exception {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getPipelineApi().cancelPipelineJobs(projectId, pipelineId);
        }
    }

    /**
     * @param projectName 项目名称
     * @param pipelineId  流水线id
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectName和pipelineId停止流水线")
    public Pipeline stopPipelineByProjectNameWithPipelineId(String projectName, Long pipelineId) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return stopPipelineByProjectIdWithPipelineId(project.getId(), pipelineId);
    }

    /**
     * @param projectId  项目id
     * @param pipelineId 流水线id
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectId和pipelineId重试流水线")
    public Pipeline retryPipelineJobByProjectIdWithPipelineId(Long projectId, Long pipelineId) throws Exception {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getPipelineApi().retryPipelineJob(projectId, pipelineId);
        }
    }

    /**
     * @param projectName 项目名称
     * @param pipelineId  流水线id
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectName和pipelineId重试流水线")
    public Pipeline retryPipelineJobByProjectNameWithPipelineId(String projectName, Long pipelineId) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return retryPipelineJobByProjectIdWithPipelineId(project.getId(), pipelineId);
    }

    /**
     * @param projectId  项目id
     * @param pipelineId 流水线id
     */
    @GitlabApiExceptionCatch(description = "根据projectId和pipelineId删除流水线")
    public void deletePipelineByProjectIdWithPipelineId(Long projectId, Long pipelineId) throws Exception {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            auth.getPipelineApi().deletePipeline(projectId, pipelineId);
        }
    }

    /**
     * @param projectName 项目名称
     * @param pipelineId  流水线id
     */
    @GitlabApiExceptionCatch(description = "根据projectName和pipelineId删除流水线")
    public void deletePipelineByProjectNameWithPipelineId(String projectName, Long pipelineId) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        deletePipelineByProjectIdWithPipelineId(project.getId(), pipelineId);
    }

    /**
     * @param projectId 项目id
     * @param ref       分支名称
     * @param variables 流水线环境变量
     * @param callback  流水线回调
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectId和ref执行流水线")
    public Pipeline executePipelineByProjectIdWithRef(Long projectId, String ref, Map<String, String> variables, GitlabPipelineJobExecuteCallback callback) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectId(projectId);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目id=%s，请先去创建项目", projectId));
        }
        int maxRetryCnt = 60;
        // =================================================================== 流水线任务开始
        // 拉起前检查是否有未结束的任务
        List<Pipeline> pipelines = describePipelineListByProjectId(projectId);
        List<Pipeline> waitStopPipelines = pipelines.stream().filter(f ->
                // RUNNING 正在运行中，尚未完成的
                PipelineStatus.RUNNING.equals(f.getStatus()) ||
                        // PENDING 正在等待资源的
                        PipelineStatus.PENDING.equals(f.getStatus()) ||
                        // CANCELED 被取消的
                        PipelineStatus.CANCELED.equals(f.getStatus()) ||
                        // MANUAL 手动停止的
                        PipelineStatus.MANUAL.equals(f.getStatus())
        ).toList();
        if (!waitStopPipelines.isEmpty()) {
            // 停止符合条件的流水线
            for (Pipeline waitStopPipeline : waitStopPipelines) {
                try {
                    stopPipelineByProjectIdWithPipelineId(waitStopPipeline.getProjectId(), waitStopPipeline.getId());
                    log.info("流水线停止成功");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    deletePipelineByProjectIdWithPipelineId(waitStopPipeline.getProjectId(), waitStopPipeline.getId());
                    log.info("流水线删除成功");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        Pipeline pipeline = createPipelineByProjectNameWithRef(project.getName(), ref, variables);
        Pipeline currentPipeline;
        while (true) {
            ThreadUtil.safeSleep(2000);
            currentPipeline = describePipelineByProjectIdWithPipelineId(pipeline.getProjectId(), pipeline.getId());
            if (PipelineStatus.PENDING.equals(currentPipeline.getStatus())) {
                if (maxRetryCnt <= 0) {
                    log.info("已达最大重试次数，删除流水线------------------------------------------------------");
                    deletePipelineByProjectIdWithPipelineId(currentPipeline.getProjectId(), currentPipeline.getId());
                    throw new BadRequestException("流水线执行失败，已达最大重试次数");
                }
                maxRetryCnt--;
                log.info("等待Runner资源中...");
                continue;
            }
            if (PipelineStatus.RUNNING.equals(currentPipeline.getStatus())) {
                log.info("流水线运行中------------------------------------------------------");
                showJobInfo(currentPipeline, callback);
                continue;
            }
            if (PipelineStatus.SUCCESS.equals(currentPipeline.getStatus())) {
                log.info("流水线执行成功------------------------------------------------------");
                showJobInfo(currentPipeline, callback);
                return currentPipeline;
            }
            if (PipelineStatus.FAILED.equals(currentPipeline.getStatus())) {
                log.info("流水线执行失败------------------------------------------------------");
                showJobInfo(currentPipeline, callback);
                throw new BadRequestException("流水线执行失败");
            }
            if (PipelineStatus.CANCELED.equals(currentPipeline.getStatus())) {
                log.info("流水线被手动停止------------------------------------------------------");
                deletePipelineByProjectIdWithPipelineId(currentPipeline.getProjectId(), currentPipeline.getId());
                throw new BadRequestException("流水线执行失败，被手动停止");
            }
        }
    }

    /**
     * @param projectName 项目名称
     * @param ref         分支名称
     * @param variables   流水线环境变量
     * @param callback    流水线回调
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectName和ref执行流水线")
    public Pipeline executePipelineByProjectNameWithRef(String projectName, String ref, Map<String, String> variables, GitlabPipelineJobExecuteCallback callback) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return executePipelineByProjectIdWithRef(project.getId(), ref, variables, callback);
    }

    private void showJobInfo(Pipeline currentPipeline, GitlabPipelineJobExecuteCallback callback) {
        List<Job> jobs = gitlabPipelineJobRepository.describeJobListByProjectIdWithPipelineId(currentPipeline.getProjectId(), currentPipeline.getId());
        log.info("-------------------------jobs content start----------------------------------");
        for (Job job : jobs) {
            log.info("id={}", job.getId());
            log.info("createdAt={}", DateUtil.formatDateTime(job.getCreatedAt()));
            log.info("startedAt={}", DateUtil.formatDateTime(job.getStartedAt()));
            log.info("duration={}", job.getDuration());
            log.info("status={}", job.getStatus());
            log.info("webUrl={}", job.getWebUrl());
            if (JobStatus.PENDING.equals(job.getStatus())) {
                log.info("stage={}挂起中", job.getStage());
            } else if (JobStatus.RUNNING.equals(job.getStatus())) {
                log.info("stage={}执行中", job.getStage());
            } else if (JobStatus.FAILED.equals(job.getStatus())) {
                log.info("stage={}执行失败", job.getStage());
            } else if (JobStatus.SUCCESS.equals(job.getStatus())) {
                log.info("stage={}执行成功", job.getStage());
            }
            callback.execute(GitlabPipelineJobInfoVo.builder()
                    .id(job.getId())
                    .createdAt(job.getCreatedAt())
                    .startedAt(job.getStartedAt())
                    .duration(job.getDuration())
                    .status(job.getStatus())
                    .webUrl(job.getWebUrl())
                    .build());
        }
        log.info("-------------------------jobs content end------------------------------------");
    }
}
