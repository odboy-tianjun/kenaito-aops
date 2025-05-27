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

import cn.odboy.app.controller.vo.ContainerdSpecConfigCreateArgs;
import cn.odboy.app.controller.vo.ContainerdSpecConfigUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesContainerdSpecConfigDO;
import cn.odboy.app.service.kubernetes.AopsKubernetesContainerdSpecConfigService;
import cn.odboy.common.model.PageArgs;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.model.DeleteByIdArgs;
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

@Api(tags = "Kubernetes集群容器规格配置")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cmdb/kubernetes/containerdSpecConfig")
public class AopsKubernetesContainerdSpecConfigController {
    private final AopsKubernetesContainerdSpecConfigService currentService;

    @ApiOperation("分页查询集群容器规格配置列表")
    @PostMapping("/describeContainerdSpecConfigPage")
    public ResponseEntity<PageResult<AopsKubernetesContainerdSpecConfigDO>> describeContainerdSpecConfigPage(@Validated @RequestBody PageArgs<AopsKubernetesContainerdSpecConfigDO> args) {
        return ResponseEntity.ok(currentService.describeContainerdSpecConfigPage(args));
    }

    @OperationLog
    @ApiOperation("创建集群容器规格配置")
    @PostMapping("/createContainerdSpecConfig")
    public ResponseEntity<Void> createContainerdSpecConfig(@Validated @RequestBody ContainerdSpecConfigCreateArgs args) {
        currentService.createContainerdSpecConfig(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("删除集群容器规格配置")
    @PostMapping("/deleteContainerdSpecConfig")
    public ResponseEntity<Void> deleteContainerdSpecConfig(@Validated @RequestBody DeleteByIdArgs args) {
        currentService.deleteContainerdSpecConfig(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("更新集群容器规格配置")
    @PostMapping("/updateContainerdSpecConfig")
    public ResponseEntity<Void> updateContainerdSpecConfig(@Validated @RequestBody ContainerdSpecConfigUpdateArgs args) {
        currentService.updateContainerdSpecConfig(args);
        return ResponseEntity.ok().build();
    }
}
