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

import cn.hutool.core.collection.CollUtil;
import cn.odboy.app.framework.gitlab.constant.GitlabDefaultConst;
import cn.odboy.app.framework.gitlab.context.GitlabApiClientManager;
import cn.odboy.app.framework.gitlab.context.GitlabCiFileAdmin;
import cn.odboy.app.framework.gitlab.context.GitlabIgnoreFileAdmin;
import cn.odboy.app.framework.gitlab.exception.GitlabApiExceptionCatch;
import cn.odboy.app.framework.gitlab.model.GitlabProjectCreateArgs;
import cn.odboy.app.framework.gitlab.model.GitlabProjectCreateResponse;
import cn.odboy.app.framework.kubernetes.model.vo.ArgsAppNameVo;
import cn.odboy.common.constant.GlobalEnvEnum;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.util.ValidationUtil;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.GroupApi;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Namespace;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.Visibility;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gitlab项目
 *
 * @author odboy
 * @date 2024-09-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabProjectRepository {
    private final GitlabApiClientManager gitlabApiClientManager;
    private final GitlabIgnoreFileAdmin gitlabIgnoreFileAdmin;
    private final GitlabCiFileAdmin gitlabCiFileAdmin;

    /**
     * @param page 当前页
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据groupId分页查询分组项目 -> ok", throwException = false)
    public List<Project> describeProjectListByGroupId(Long groupId, int page) {
        int newPage = page <= 0 ? 1 : page;
        List<Project> list = new ArrayList<>();
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            GroupApi groupApi = client.getGroupApi();
            return groupApi.getProjects(groupId, newPage, 10000);
        }
    }

    /**
     * @param projectId /
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据projectId查询项目 -> ok", throwException = false)
    public Project describeProjectByProjectId(Long projectId) {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjectsStream().filter(f -> f.getId().equals(projectId)).findFirst().orElse(null);
        }
    }

    /**
     * @param appName /
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据appName查询项目 -> ok", throwException = false)
    public Project describeProjectByProjectName(String appName) {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjectsStream().filter(f -> f.getPath().equals(appName)).findFirst().orElse(null);
        }
    }

    /**
     * @param currentPage 当前页
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "分页查询项目列表 -> ok", throwException = false)
    public List<Project> describeProjectListByCurrentPage(int currentPage) {
        int newPage = currentPage <= 0 ? 1 : currentPage;
        List<Project> list = new ArrayList<>();
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjects(newPage, 100);
        }
    }

    /**
     * @param appName 应用名称
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据appName查询项目列表 -> ok", throwException = false)
    public List<Project> describeProjectListByAppName(String appName) {
        List<Project> list = new ArrayList<>();
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjectsStream(appName).collect(Collectors.toList());
        }
    }

    /**
     * @param key 关键字
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "根据关键字查询项目列表 -> ok", throwException = false)
    public List<Project> describeProjectListByKey(String key) {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjects(key, 1, 20);
        }
    }

    private GitlabProjectCreateResponse transformCreateResp(GitlabProjectCreateArgs args, Project newProject, String newProjectName) {
        GitlabProjectCreateResponse createResp = new GitlabProjectCreateResponse();
        createResp.setCreatorId(newProject.getCreatorId());
        createResp.setCreatedAt(newProject.getCreatedAt());
        createResp.setDefaultBranch(newProject.getDefaultBranch());
        createResp.setHttpUrlToRepo(newProject.getHttpUrlToRepo());
        createResp.setProjectId(newProject.getId());
        createResp.setProjectName(newProject.getName());
        createResp.setVisibility(newProject.getVisibility().name());
        createResp.setHomeUrl(newProject.getWebUrl());
        createResp.setName(args.getName());
        createResp.setAppName(newProjectName);
        createResp.setDescription(args.getDescription());
        return createResp;
    }

    /**
     * @param args /
     * @return /
     */
    @GitlabApiExceptionCatch(description = "创建项目 -> ok")
    public GitlabProjectCreateResponse createProject(GitlabProjectCreateArgs args) throws Exception {
        ValidationUtil.validate(args);
        String appName = args.getAppName();
        String newProjectName = new ArgsAppNameVo(appName).getValue().trim();
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            ProjectApi projectApi = client.getProjectApi();
            Optional<Project> loadProjectOptional = projectApi.getProjectsStream().filter(f -> f.getPath().equals(args.getAppName())).findFirst();
            if (loadProjectOptional.isPresent()) {
                throw new BadRequestException("应用名称已存在");
            }
            Project project = new Project();
            project.setName(StrUtil.isBlank(args.getName()) ? newProjectName : args.getName());
            project.setPath(newProjectName);
            project.setDescription(args.getDescription());
            project.setVisibility(Visibility.PRIVATE);
            project.setInitializeWithReadme(true);
            project.setDefaultBranch(GitlabDefaultConst.PROJECT_DEFAULT_BRANCH);
            // Groups Or Users -> namespace id
            Namespace namespace = client.getNamespaceApi().getNamespace(args.getGroupOrUserId());
            project.setNamespace(namespace);
            Project newProject = projectApi.createProject(args.getGroupOrUserId(), project);
            return transformCreateResp(args, newProject, newProjectName);
        }
    }

    /**
     * @param projectId   项目id
     * @param userId      用户id
     * @param accessLevel 访问级别
     */
    @GitlabApiExceptionCatch(description = "新增项目成员 -> ok")
    public void addProjectMember(Long projectId, Long userId, AccessLevel accessLevel) throws Exception {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            ProjectApi projectApi = client.getProjectApi();
            projectApi.addMember(projectId, userId, accessLevel);
        }
    }

    /**
     * @param projectId   项目id
     * @param userIds     用户id列表
     * @param accessLevel 访问级别
     */
    @GitlabApiExceptionCatch(description = "批量新增项目成员 -> ok")
    public void addProjectMembers(Long projectId, List<Long> userIds, AccessLevel accessLevel) throws Exception {
        if (CollUtil.isNotEmpty(userIds)) {
            for (Long userId : userIds.stream().filter(Objects::nonNull).distinct().toList()) {
                try {
                    this.addProjectMember(projectId, userId, accessLevel);
                } catch (Exception e) {
                    log.error("新增应用成员失败", e);
                }
            }
        }
    }

    /**
     * @param projectId 项目id
     * @param userId    用户id
     */
    @GitlabApiExceptionCatch(description = "移除项目成员 -> ok")
    public void deleteProjectMember(Long projectId, Long userId) throws Exception {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            ProjectApi projectApi = client.getProjectApi();
            projectApi.removeMember(projectId, userId);
        }
    }

    /**
     * @param projectId 项目id
     * @param userIds   用户id列表
     */
    @GitlabApiExceptionCatch(description = "批量移除项目成员 -> ok")
    public void deleteProjectMembers(Long projectId, List<Long> userIds) throws Exception {
        if (CollUtil.isNotEmpty(userIds)) {
            for (Long userId : userIds.stream().filter(Objects::nonNull).distinct().toList()) {
                try {
                    deleteProjectMember(projectId, userId);
                } catch (Exception e) {
                    log.error("移除应用成员失败", e);
                }
            }
        }
    }


    /**
     * @param projectId /
     */
    @GitlabApiExceptionCatch(description = "根据projectId删除项目 -> ok")
    public void deleteProjectByProjectId(Long projectId) throws Exception {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            ProjectApi projectApi = client.getProjectApi();
            projectApi.deleteProject(projectId);
        } catch (Exception e) {
            log.error("根据projectId删除应用失败", e);
            throw new BadRequestException("根据projectId删除应用失败");
        }
    }

    @GitlabApiExceptionCatch(description = "根据projectId集合删除项目 -> ok")
    public void deleteProjectsByProjectIds(List<Long> projectIds) throws Exception {
        if (CollUtil.isNotEmpty(projectIds)) {
            for (Long projectId : projectIds) {
                try {
                    deleteProjectByProjectId(projectId);
                } catch (Exception e) {
                    log.error("根据projectId删除应用失败", e);
                }
            }
        }
    }

    /**
     * @param appName /
     */
    @GitlabApiExceptionCatch(description = "根据appName删除项目 -> ok")
    public void deleteProjectByProjectName(String appName) throws Exception {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            Project localProject = describeProjectByProjectName(appName);
            if (localProject == null) {
                throw new BadRequestException("应用不存在");
            }
            ProjectApi projectApi = client.getProjectApi();
            projectApi.deleteProject(localProject.getId());
        }
    }

    @GitlabApiExceptionCatch(description = "根据appName集合删除项目 -> ok")
    public void deleteProjectsByProjectNames(List<String> appNames) throws Exception {
        if (CollUtil.isNotEmpty(appNames)) {
            for (String appName : appNames) {
                try {
                    deleteProjectByProjectName(appName);
                } catch (Exception e) {
                    log.error("根据appName删除应用失败", e);
                }
            }
        }
    }

    @GitlabApiExceptionCatch(description = "初始化GitIgnore文件 -> ok")
    public void initGitIgnoreFile(Long projectId, String defaultBranchName, String language) throws Exception {
        String fileContent = gitlabIgnoreFileAdmin.getContent(language);
        if (cn.hutool.core.util.StrUtil.isBlank(language)) {
            log.error("不支持的语言, 跳过 .gitignore 文件初始化");
            return;
        }
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            RepositoryFile gitlabApiClientManagerFile = new RepositoryFile();
            gitlabApiClientManagerFile.setFilePath(".gitignore");
            gitlabApiClientManagerFile.setContent(fileContent);
            client.getRepositoryFileApi().createFile(projectId, gitlabApiClientManagerFile, defaultBranchName, "init .gitignore");
            log.info("初始化 .gitignore 文件成功");
        }
    }

    @GitlabApiExceptionCatch(description = "初始化GitCI文件 -> ok")
    public void initGitCiFile(Long projectId, String defaultBranch, String language, String appName) throws Exception {
        if (cn.hutool.core.util.StrUtil.isBlank(language)) {
            log.error("不支持的语言, 跳过 .gitlab-ci.yml 文件初始化");
            return;
        }
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            String fileContent = gitlabCiFileAdmin.getCiFileContent(language);
            RepositoryFile gitlabApiClientManagerFile = new RepositoryFile();
            gitlabApiClientManagerFile.setFilePath(".gitlab-ci.yml");
            gitlabApiClientManagerFile.setContent(fileContent);
            client.getRepositoryFileApi().createFile(projectId, gitlabApiClientManagerFile, defaultBranch, "init .gitlab-ci.yml");
            log.info("初始化 .gitlab-ci.yml 文件成功");
        } catch (GitLabApiException e) {
            log.error("初始化 .gitlab-ci.yml 文件失败", e);
        }
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            GlobalEnvEnum[] allEnvList = new GlobalEnvEnum[]{GlobalEnvEnum.Daily, GlobalEnvEnum.Stage, GlobalEnvEnum.Online};
            for (GlobalEnvEnum envEnum : allEnvList) {
                String filePath = "Dockerfile_" + envEnum.getCode();
                try {
                    String dockerfileContent = gitlabCiFileAdmin.getDockerfileContent(language, envEnum);
                    dockerfileContent = dockerfileContent.replaceAll("#APP_NAME#", appName);
                    RepositoryFile gitlabApiClientManagerFile = new RepositoryFile();
                    gitlabApiClientManagerFile.setFilePath(filePath);
                    gitlabApiClientManagerFile.setContent(dockerfileContent);
                    client.getRepositoryFileApi().createFile(projectId, gitlabApiClientManagerFile, defaultBranch, "init " + filePath);
                    log.info("初始化 {} 文件成功", filePath);
                } catch (GitLabApiException e) {
                    log.error("初始化 {} 文件失败", filePath, e);
                }
            }
        }
        String releaseFileName = appName + ".release";
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            String dockerfileContent = gitlabCiFileAdmin.getReleaseFileContent(language);
            dockerfileContent = dockerfileContent.replaceAll("#APP_NAME#", appName);
            RepositoryFile gitlabApiClientManagerFile = new RepositoryFile();
            gitlabApiClientManagerFile.setFilePath(releaseFileName);
            gitlabApiClientManagerFile.setContent(dockerfileContent);
            client.getRepositoryFileApi().createFile(projectId, gitlabApiClientManagerFile, defaultBranch, "init release");
            log.info("初始化 release 文件成功");
        } catch (GitLabApiException e) {
            log.error("初始化 release 文件失败", e);
        }
    }
}
