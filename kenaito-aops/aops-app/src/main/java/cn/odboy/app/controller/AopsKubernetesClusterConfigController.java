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

import cn.odboy.app.controller.vo.ClusterConfigCreateArgs;
import cn.odboy.app.controller.vo.ClusterConfigModifyDefaultAppYmlArgs;
import cn.odboy.app.controller.vo.ClusterConfigUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesClusterConfigDO;
import cn.odboy.app.service.kubernetes.AopsKubernetesClusterConfigService;
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

@Api(tags = "Kubernetes集群配置管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cmdb/kubernetes/clusterConfig")
public class AopsKubernetesClusterConfigController {
    private final AopsKubernetesClusterConfigService currentService;

    @ApiOperation("分页查询集群配置列表")
    @PostMapping("/describeClusterConfigPage")
    public ResponseEntity<PageResult<AopsKubernetesClusterConfigDO>> describeClusterConfigPage(@Validated @RequestBody PageArgs<AopsKubernetesClusterConfigDO> args) {
        return ResponseEntity.ok(currentService.describeClusterConfigPage(args));
    }

    @OperationLog
    @ApiOperation("创建集群配置")
    @PostMapping("/createClusterConfig")
    public ResponseEntity<Void> createClusterConfig(@Validated @RequestBody ClusterConfigCreateArgs args) {
        currentService.createClusterConfig(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("删除集群配置")
    @PostMapping("/deleteClusterConfig")
    public ResponseEntity<Void> deleteClusterConfig(@Validated @RequestBody DeleteByIdArgs args) {
        currentService.deleteClusterConfig(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("更新集群配置")
    @PostMapping("/updateClusterConfig")
    public ResponseEntity<Void> updateClusterConfig(@Validated @RequestBody ClusterConfigUpdateArgs args) {
        currentService.updateClusterConfig(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("修改集群默认应用负载配置")
    @PostMapping("/modifyClusterDefaultAppYml")
    public ResponseEntity<Void> modifyClusterDefaultAppYml(@Validated @RequestBody ClusterConfigModifyDefaultAppYmlArgs args) {
        currentService.modifyClusterDefaultAppYml(args);
        return ResponseEntity.ok().build();
    }
}
