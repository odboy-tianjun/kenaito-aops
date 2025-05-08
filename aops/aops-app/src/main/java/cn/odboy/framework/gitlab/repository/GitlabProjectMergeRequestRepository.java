/*
 *  Copyright 2021-2025 Tian Jun
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
package cn.odboy.framework.gitlab.repository;

import cn.hutool.core.util.StrUtil;
import cn.odboy.exception.BadRequestException;
import cn.odboy.framework.gitlab.constant.GitlabDefaultConst;
import cn.odboy.framework.gitlab.context.GitlabApiClientManager;
import cn.odboy.framework.gitlab.exception.GitlabApiExceptionCatch;
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
     * 合并请求是否存在冲突
     *
     * @param mergeRequest /
     * @return /
     */
    public boolean isMergeRequestHasConflict(MergeRequest mergeRequest) {
        if (mergeRequest == null) {
            throw new BadRequestException("参数mergeRequest不能为空");
        }
        return "opened".equals(mergeRequest.getState()) && "conflict".equals(mergeRequest.getDetailedMergeStatus()) && mergeRequest.getHasConflicts();
    }

    /**
     * 合并请求是否可以Accept
     *
     * @param mergeRequest /
     * @return /
     */
    public boolean isMergeRequestCanAccept(MergeRequest mergeRequest) {
        if (mergeRequest == null) {
            throw new BadRequestException("参数mergeRequest不能为空");
        }
        return "opened".equals(mergeRequest.getState()) && "mergeable".equals(mergeRequest.getDetailedMergeStatus()) && !mergeRequest.getHasConflicts();
    }

    /**
     * 合并请求是否已经合并
     *
     * @param mergeRequest /
     * @return /
     */
    public boolean isMergeRequestMerged(MergeRequest mergeRequest) {
        if (mergeRequest == null) {
            throw new BadRequestException("参数mergeRequest不能为空");
        }
        return "merged".equals(mergeRequest.getState()) && "not_open".equals(mergeRequest.getDetailedMergeStatus());
    }

    /**
     * 正在检查是否可以合并
     *
     * @param mergeRequest /
     * @return /
     */
    public boolean isMergeRequestChecking(MergeRequest mergeRequest) {
        if (mergeRequest == null) {
            throw new BadRequestException("参数mergeRequest不能为空");
        }
        return "opened".equals(mergeRequest.getState()) && "checking".equals(mergeRequest.getDetailedMergeStatus());
    }

    /**
     * @param projectName     项目名称
     * @param mergeRequestIid 合并请求iid
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "获取合并请求详情", throwException = false)
    public MergeRequest describeMergeRequest(String projectName, Long mergeRequestIid) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describeMergeRequest(project.getId(), mergeRequestIid);
    }

    /**
     * @param projectId       项目Id
     * @param mergeRequestIid 合并请求iid
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "获取分支合并请求详情", throwException = false)
    public MergeRequest describeMergeRequest(Long projectId, Long mergeRequestIid) {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getMergeRequestApi().getMergeRequest(projectId, mergeRequestIid);
        }
    }

    /**
     * @param projectName 项目名称
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "获取Opened状态的合并请求列表", throwException = false)
    public List<MergeRequest> listOpenedMergeRequests(String projectName) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return listOpenedMergeRequests(project.getId());
    }

    /**
     * @param projectId 项目Id
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "获取Opened状态的合并请求列表", throwException = false)
    public List<MergeRequest> listOpenedMergeRequests(Long projectId) {
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
    @GitlabApiExceptionCatch(description = "创建分支合并请求")
    public MergeRequest createMergeRequest(String projectName, String sourceBranch, String targetBranch, String title, String description) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return createMergeRequest(project.getId(), sourceBranch, targetBranch, title, description);
    }

    /**
     * @param projectId    项目Id
     * @param sourceBranch 源分支名称
     * @param targetBranch 目标分支名称
     * @param title        简短标题
     * @param description  详细说明
     */
    @GitlabApiExceptionCatch(description = "创建分支合并请求")
    public MergeRequest createMergeRequest(Long projectId, String sourceBranch, String targetBranch, String title, String description) throws Exception {
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
    @GitlabApiExceptionCatch(description = "接受分支合并请求")
    public MergeRequest acceptMergeRequest(String projectName, MergeRequest mergeRequest) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return acceptMergeRequest(project.getId(), mergeRequest);
    }

    /**
     * @param projectId    项目Id
     * @param mergeRequest 合并请求
     * @return /
     */
    @GitlabApiExceptionCatch(description = "接受分支合并请求")
    public MergeRequest acceptMergeRequest(Long projectId, MergeRequest mergeRequest) throws Exception {
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
    @GitlabApiExceptionCatch(description = "关闭分支合并请求")
    public MergeRequest closeMergeRequest(String projectName, Long mergeRequestIid) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return closeMergeRequest(project.getId(), mergeRequestIid);
    }

    /**
     * @param projectId       项目Id
     * @param mergeRequestIid 合并请求iid
     * @return /
     */
    @GitlabApiExceptionCatch(description = "关闭分支合并请求")
    public MergeRequest closeMergeRequest(Long projectId, Long mergeRequestIid) throws Exception {
        try (GitLabApi auth = gitlabApiClientManager.getClient()) {
            return auth.getMergeRequestApi().cancelMergeRequest(projectId, mergeRequestIid);
        }
    }
}
