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

import cn.odboy.app.framework.gitlab.context.GitlabApiClientManager;
import cn.odboy.app.framework.gitlab.exception.GitlabApiExceptionCatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GroupApi;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.GroupParams;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * gitlab group
 *
 * @author odboy
 * @date 2025-01-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabGroupRepository {
    private final GitlabApiClientManager gitlabApiClientManager;

    /**
     * @param groupName /
     * @return /
     */
    @GitlabApiExceptionCatch(description = "创建Git分组 -> ok")
    public Group createGroup(String groupName) throws Exception {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            GroupApi groupApi = client.getGroupApi();
            GroupParams groupParams = new GroupParams();
            groupParams.withName(groupName);
            groupParams.withPath(groupName);
            // 私有分组
            groupParams.withVisibility("0");
            return groupApi.createGroup(groupParams);
        }
    }

    /**
     * @param groupName /
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "通过groupName查Git分组 -> ok", throwException = false)
    public Group describeGroupByGroupName(String groupName) {
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            GroupApi groupApi = client.getGroupApi();
            return groupApi.getGroupsStream().filter(f -> f.getName().equals(groupName)).findFirst().orElse(null);
        }
    }

    /**
     * @param page 当前页
     * @return /
     */
    @SneakyThrows
    @GitlabApiExceptionCatch(description = "分页获取Git分组 -> ok", throwException = false)
    public List<Group> describeGroupList(int page) {
        int newPage = page <= 0 ? 1 : page;
        List<Group> list = new ArrayList<>();
        try (GitLabApi client = gitlabApiClientManager.getClient()) {
            GroupApi groupApi = client.getGroupApi();
            return groupApi.getGroups(newPage, 10000);
        }
    }
}
