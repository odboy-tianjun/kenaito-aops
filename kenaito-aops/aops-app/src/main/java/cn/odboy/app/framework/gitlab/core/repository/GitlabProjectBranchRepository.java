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

import cn.hutool.core.util.StrUtil;
import cn.odboy.app.framework.gitlab.core.context.GitlabApiClientManager;
import cn.odboy.app.framework.gitlab.core.exception.GitlabApiExceptionCatch;
import cn.odboy.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.CompareResults;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.Project;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Gitlab仓库
 *
 * @author odboy
 * @date 2024-09-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabProjectBranchRepository {
    private final GitlabApiClientManager gitlabApiClientManager;
    private final GitlabProjectRepository gitlabProjectRepository;

    /**
     * @param projectId  项目id
     * @param branchName 分支名称
     * @param ref        从什么分支创建, 比如: master
     * @return /
     */
    @GitlabApiExceptionCatch(description = "创建分支")
    public Branch createBranch(Long projectId, String branchName, String ref) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectId(projectId);
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            RepositoryApi gitlabApiClientManagerApi = client.getRepositoryApi();
            if (StrUtil.isBlank(ref)) {
                ref = project.getDefaultBranch();
            }
            return gitlabApiClientManagerApi.createBranch(projectId, branchName, ref);
        }
    }

    /**
     * @param projectName 项目名称
     * @param branchName  分支名称
     * @param ref         从什么分支创建, 比如: master
     * @return /
     */
    @GitlabApiExceptionCatch(description = "创建分支")
    public Branch createBranch(String projectName, String branchName, String ref) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return createBranch(project.getId(), branchName, ref);
    }

    /**
     * @param projectId  项目id
     * @param branchName 分支名称
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId和branchName获取分支", throwException = false)
    public Branch describeBranchByProjectIdWithBranchName(Long projectId, String branchName) {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            return client.getRepositoryApi().getBranch(projectId, branchName);
        }
    }

    /**
     * @param projectName 项目名称
     * @param branchName  分支名称
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectName和branchName获取分支", throwException = false)
    public Branch describeBranchByProjectNameWithBranchName(String projectName, String branchName) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describeBranchByProjectIdWithBranchName(project.getId(), branchName);
    }

    /**
     * @param projectId 项目id
     * @param searchKey 关键字
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId和searchKey查询分支列表", throwException = false)
    public List<Branch> describeBranchListByProjectIdWithSearchKey(Long projectId, String searchKey) {
        List<Branch> list = new ArrayList<>();
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            Pager<Branch> pager = client.getRepositoryApi().getBranches(projectId, searchKey, 100);
            while (pager.hasNext()) {
                list.addAll(pager.next());
            }
        }
        return list;
    }

    /**
     * @param projectName 项目名称
     * @param searchKey   关键字
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "关键字分页获取分支", throwException = false)
    public List<Branch> describeBranchListByProjectNameWithSearchKey(String projectName, String searchKey) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describeBranchListByProjectIdWithSearchKey(project.getId(), searchKey);
    }

    /**
     * @param projectId  项目id
     * @param branchName 分支名称
     */
    @GitlabApiExceptionCatch(description = "根据projectId和branchName删除分支")
    public void deleteBranchByProjectIdWithBranchName(Long projectId, String branchName) throws Exception {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            client.getRepositoryApi().deleteBranch(projectId, branchName);
        }
    }

    /**
     * @param projectName 项目名称
     * @param branchName  分支名称
     */
    @GitlabApiExceptionCatch(description = "根据projectName和branchName删除分支")
    public void deleteBranchByProjectName(String projectName, String branchName) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        deleteBranchByProjectIdWithBranchName(project.getId(), branchName);
    }

    /**
     * @param projectId    项目id
     * @param sourceBranch 来源分支
     * @param targetBranch 目标分支
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId比较分支差异(合并分支之前, 展示改动的内容)")
    public CompareResults compareBranchByProjectId(Long projectId, String sourceBranch, String targetBranch) {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            // 比较两个分支
            CompareResults compareResults = client.getRepositoryApi().compare(projectId, sourceBranch, targetBranch);
            // 输出差异信息
            System.out.println("Commits:");
            for (Commit commit : compareResults.getCommits()) {
                System.out.println("  " + commit.getId() + " - " + commit.getMessage());
            }
            System.out.println("Diffs:");
            for (Diff diff : compareResults.getDiffs()) {
                System.out.println("  " + diff.getOldPath() + " -> " + diff.getNewPath());
            }
            return compareResults;
        }
    }

    /**
     * @param projectName  项目名称
     * @param sourceBranch 源分支
     * @param targetBranch 目标分支
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectName比较分支差异(合并分支之前, 展示改动的内容)")
    public CompareResults compareBranchByProjectName(String projectName, String sourceBranch, String targetBranch) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return compareBranchByProjectId(project.getId(), sourceBranch, targetBranch);
    }
}
