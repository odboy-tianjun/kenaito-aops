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
package cn.odboy.app.controller.cmdb;

import cn.odboy.app.controller.cmdb.vo.CreateClusterConfigArgs;
import cn.odboy.app.controller.cmdb.vo.ModifyClusterDefaultAppYmlArgs;
import cn.odboy.app.controller.cmdb.vo.UpdateClusterConfigArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesClusterConfigDO;
import cn.odboy.app.service.kubernetes.AopsKubernetesClusterConfigService;
import cn.odboy.common.pojo.PageArgs;
import cn.odboy.common.pojo.vo.RemoveByIdArgs;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Kubernetes集群配置 前端控制器
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aops/kubernetes/clusterConfig")
public class AopsKubernetesClusterConfigController {
    private final AopsKubernetesClusterConfigService aopsKubernetesClusterConfigService;

    @ApiOperation("分页查询Kubernetes集群配置")
    @PostMapping("/describePage")
    @PreAuthorize("@el.check('aops:kubernetes:clusterConfig:list')")
    public ResponseEntity<Object> describePage(@Validated @RequestBody PageArgs<AopsKubernetesClusterConfigDO> args) {
        return new ResponseEntity<>(aopsKubernetesClusterConfigService.describeKubernetesClusterConfigPage(args), HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("新增Kubernetes集群配置")
    @PostMapping("/createClusterConfig")
    @PreAuthorize("@el.check('aops:kubernetes:clusterConfig:update')")
    public ResponseEntity<Object> createClusterConfig(@Validated @RequestBody CreateClusterConfigArgs args) {
        aopsKubernetesClusterConfigService.createClusterConfig(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("删除集群配置")
    @PostMapping("/removeClusterConfig")
    @PreAuthorize("@el.check('aops:kubernetes:clusterConfig:remove')")
    public ResponseEntity<Object> removeClusterConfig(@Validated @RequestBody RemoveByIdArgs args) {
        aopsKubernetesClusterConfigService.removeClusterConfig(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("修改Kubernetes集群配置")
    @PostMapping("/updateClusterConfig")
    @PreAuthorize("@el.check('aops:kubernetes:clusterConfig:update')")
    public ResponseEntity<Object> updateClusterConfig(@Validated @RequestBody UpdateClusterConfigArgs args) {
        aopsKubernetesClusterConfigService.updateClusterConfig(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("修改Kubernetes集群默认应用负载配置")
    @PostMapping("/modifyClusterDefaultAppYml")
    @PreAuthorize("@el.check('aops:kubernetes:clusterConfig:update')")
    public ResponseEntity<Object> modifyClusterDefaultAppYml(@Validated @RequestBody ModifyClusterDefaultAppYmlArgs args) {
        aopsKubernetesClusterConfigService.modifyClusterDefaultAppYml(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
