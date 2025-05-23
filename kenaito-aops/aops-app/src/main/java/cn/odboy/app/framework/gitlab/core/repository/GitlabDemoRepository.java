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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.springframework.stereotype.Component;

/**
 * gitlab Demo
 *
 * @author odboy
 * @date 2025-01-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabDemoRepository {
    private final GitlabApiClientManager gitlabApiClientManager;


    @GitlabApiExceptionCatch(description = "例子")
    public void test() throws Exception {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            client.getJobApi().cancelJob(null, null);
        }
    }
}
