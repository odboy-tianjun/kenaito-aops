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

import cn.odboy.app.controller.vo.AppCreateArgs;
import cn.odboy.app.controller.vo.AppModifyMetaArgs;
import cn.odboy.app.dal.dataobject.AopsAppDO;
import cn.odboy.app.framework.gitlab.core.repository.GitlabGroupRepository;
import cn.odboy.app.framework.gitlab.core.repository.GitlabProjectRepository;
import cn.odboy.app.service.app.AopsAppService;
import cn.odboy.common.pojo.PageArgs;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.common.pojo.vo.DeleteByIdArgs;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "应用管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cmdb/appManage")
public class AopsCmdbAppManageController {
    private final AopsAppService currentService;

    @ApiOperation("分页查询应用列表")
    @PostMapping("/describeAppPage")
    @PreAuthorize("@el.check()")
    public ResponseEntity<PageResult<AopsAppDO>> describeAppPage(@Validated @RequestBody PageArgs<AopsAppDO> args) {
        return ResponseEntity.ok(currentService.describeAppPage(args));
    }

    @OperationLog
    @ApiOperation("创建应用")
    @PostMapping("/createApp")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Void> createApp(@Validated @RequestBody AppCreateArgs args) throws Exception {
        currentService.createApp(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("删除应用")
    @PostMapping("/deleteApp")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Void> deleteApp(@Validated @RequestBody DeleteByIdArgs args) {
        currentService.deleteApp(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("更新应用")
    @PostMapping("/updateApp")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Void> updateApp(@Validated @RequestBody AppModifyMetaArgs args) {
        currentService.updateApp(args);
        return ResponseEntity.ok().build();
    }
}
