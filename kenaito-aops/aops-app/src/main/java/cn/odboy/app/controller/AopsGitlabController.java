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
package cn.odboy.app.controller;

import cn.odboy.app.controller.vo.GitlabQueryRepositoryByGroupIdArgs;
import cn.odboy.app.framework.gitlab.core.repository.GitlabGroupRepository;
import cn.odboy.app.framework.gitlab.core.repository.GitlabProjectRepository;
import cn.odboy.common.model.LongMetaOptionItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "Gitlab管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gitlab")
public class AopsGitlabController {
    private final GitlabGroupRepository gitlabGroupRepository;
    private final GitlabProjectRepository gitlabProjectRepository;

    @ApiOperation("查询Gitlab分组元数据")
    @PostMapping("/describeGroupMetadata")
    public ResponseEntity<List<LongMetaOptionItem>> describeGroupMetadata() {
        List<Group> groups = gitlabGroupRepository.describeGroupList(1);
        List<LongMetaOptionItem> metaOptionItems = groups.stream().map(m -> LongMetaOptionItem.builder()
                .label(m.getName())
                .value(m.getId())
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok(metaOptionItems);
    }

    @ApiOperation("根据Gitlab分组查询Gitlab仓库元数据")
    @PostMapping("/describeRepositoryMetadata")
    public ResponseEntity<List<LongMetaOptionItem>> describeRepositoryMetadata(@Validated @RequestBody GitlabQueryRepositoryByGroupIdArgs args) {
        List<Project> projects = gitlabProjectRepository.describeProjectListByGroupId(args.getGroupId(), 1);
        List<LongMetaOptionItem> metaOptionItems = projects.stream().map(m -> LongMetaOptionItem.builder()
                .label(m.getHttpUrlToRepo())
                .value(m.getId())
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok(metaOptionItems);
    }
}
