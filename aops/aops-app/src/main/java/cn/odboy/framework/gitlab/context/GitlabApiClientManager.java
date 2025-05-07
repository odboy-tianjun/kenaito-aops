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
package cn.odboy.framework.gitlab.context;

import cn.odboy.exception.BadRequestException;
import cn.odboy.framework.gitlab.config.GitlabProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Version;
import org.springframework.stereotype.Component;

/**
 * gitlab 客户端认证
 *
 * @author odboy
 * @date 2025-01-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabApiClientManager {
    private final GitlabProperties gitlabProperties;

    public GitLabApi getClient() {
        GitLabApi gitLabApi = new GitLabApi(gitlabProperties.getUrl(), gitlabProperties.getAccessToken());
        gitLabApi.setRequestTimeout(20000, 20000);
        try {
            Version version = gitLabApi.getVersion();
            log.info("GitlabAPI初始化成功. 当前版本为: {}", version.getVersion());
        } catch (Exception e) {
            log.error("GitlabAPI初始化失败, 请检查gitlab与项目配置", e);
            throw new BadRequestException("GitlabAPI初始化失败, 请检查gitlab与项目配置");
        }
        return gitLabApi;
    }
}
