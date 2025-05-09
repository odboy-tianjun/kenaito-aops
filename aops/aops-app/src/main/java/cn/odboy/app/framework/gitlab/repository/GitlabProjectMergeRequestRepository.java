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
package cn.odboy.app.framework.gitlab.repository;

import cn.hutool.core.util.StrUtil;
import cn.odboy.app.framework.gitlab.context.GitlabApiClientManager;
import cn.odboy.app.framework.gitlab.exception.GitlabApiExceptionCatch;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.app.framework.gitlab.constant.GitlabDefaultConst;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.MergeRequestFilter;
import org.gitlab4j.api.models.Project;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 分支合并请求
 *
 * @author odboy
 * @date 2025-01-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabProjectMergeRequestRepository {
    private final GitlabApiClientManager gitlabApiClientManager;
    private final GitlabProjectRepository gitlabProjectRepository;


    /**
     * @param projectName     项目名称
     * @param mergeRequestIid 合并请求iid
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectName和mergeRequestIid查询合并请求详情", throwException = false)
    public MergeRequest describeMergeRequestByProjectNameWithMergeRequestIid(String projectName, Long mergeRequestIid) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describeMergeRequestByProjectIdWithMergeRequestIid(project.getId(), mergeRequestIid);
    }

    /**
     * @param projectId       项目Id
     * @param mergeRequestIid 合并请求iid
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId和mergeRequestIid查询合并请求详情", throwException = false)
    public MergeRequest describeMergeRequestByProjectIdWithMergeRequestIid(Long projectId, Long mergeRequestIid) {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getMergeRequestApi().getMergeRequest(projectId, mergeRequestIid);
        }
    }

    /**
     * @param projectName 项目名称
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectName查询Opened状态的合并请求列表", throwException = false)
    public List<MergeRequest> describeOpenedMergeRequestListByProjectName(String projectName) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describeOpenedMergeRequestListByProjectId(project.getId());
    }

    /**
     * @param projectId 项目Id
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId查询Opened状态的合并请求列表", throwException = false)
    public List<MergeRequest> describeOpenedMergeRequestListByProjectId(Long projectId) {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            MergeRequestFilter filter = new MergeRequestFilter();
            filter.setProjectId(projectId);
            filter.setState(Constants.MergeRequestState.OPENED);
            return auth.getMergeRequestApi().getMergeRequests(filter);
        }
    }

    /**
     * @param projectName  项目名称
     * @param sourceBranch 源分支名称
     * @param targetBranch 目标分支名称
     * @param title        简短标题
     * @param description  详细说明
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectName创建分支合并请求")
    public MergeRequest createMergeRequestByProjectName(String projectName, String sourceBranch, String targetBranch, String title, String description) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return createMergeRequestByProjectId(project.getId(), sourceBranch, targetBranch, title, description);
    }

    /**
     * @param projectId    项目Id
     * @param sourceBranch 源分支名称
     * @param targetBranch 目标分支名称
     * @param title        简短标题
     * @param description  详细说明
     */
    @GitlabApiExceptionCatch(description = "根据projectId创建分支合并请求")
    public MergeRequest createMergeRequestByProjectId(Long projectId, String sourceBranch, String targetBranch, String title, String description) throws Exception {
        if (StrUtil.isBlank(title)) {
            title = String.format("%s 合并到 %s", sourceBranch, targetBranch);
        }
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            // 这里有个问题，如果不指定assigneeId（处理人）的话，那么合并请求会在冲突解决的同时，自动接受合并请求;
            // 所以，如果assigneeId=null的时候，无需调用acceptMergeRequest方法
            return auth.getMergeRequestApi().createMergeRequest(projectId, sourceBranch, targetBranch, title, description, GitlabDefaultConst.ROOT_NAMESPACE_ID);
        }
    }

    /**
     * @param projectName  项目名称
     * @param mergeRequest 合并请求
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectName接受分支合并请求")
    public MergeRequest acceptMergeRequestByProjectName(String projectName, MergeRequest mergeRequest) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return acceptMergeRequestByProjectId(project.getId(), mergeRequest);
    }

    /**
     * @param projectId    项目Id
     * @param mergeRequest 合并请求
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectId接受分支合并请求")
    public MergeRequest acceptMergeRequestByProjectId(Long projectId, MergeRequest mergeRequest) throws Exception {
        String mergeCommitMessage = mergeRequest.getTitle() == null ? mergeRequest.getDescription() : mergeRequest.getTitle();
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getMergeRequestApi().acceptMergeRequest(projectId, mergeRequest.getIid(), mergeCommitMessage, false, false);
        }
    }

    /**
     * @param projectName     项目名称
     * @param mergeRequestIid 合并请求iid
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectName和mergeRequestIid关闭分支合并请求")
    public MergeRequest closeMergeRequestByProjectNameWithMergeRequestIid(String projectName, Long mergeRequestIid) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return closeMergeRequestByProjectIdWithMergeRequestIid(project.getId(), mergeRequestIid);
    }

    /**
     * @param projectId       项目Id
     * @param mergeRequestIid 合并请求iid
     * @return /
     */
    @GitlabApiExceptionCatch(description = "根据projectId和mergeRequestIid关闭分支合并请求")
    public MergeRequest closeMergeRequestByProjectIdWithMergeRequestIid(Long projectId, Long mergeRequestIid) throws Exception {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getMergeRequestApi().cancelMergeRequest(projectId, mergeRequestIid);
        }
    }
}
