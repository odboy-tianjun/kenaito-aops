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

import cn.odboy.app.controller.vo.NetworkIngressCreateArgs;
import cn.odboy.app.controller.vo.NetworkIngressUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesNetworkIngressDO;
import cn.odboy.app.service.kubernetes.AopsKubernetesNetworkIngressService;
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

@Api(tags = "Kubernetes集群Ingress")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cmdb/kubernetes/network")
public class AopsKubernetesNetworkIngressController {
    private final AopsKubernetesNetworkIngressService currentService;

    @ApiOperation("分页查询Ingress列表")
    @PostMapping("/describeIngressPage")
    public ResponseEntity<PageResult<AopsKubernetesNetworkIngressDO>> describeIngressPage(@Validated @RequestBody PageArgs<AopsKubernetesNetworkIngressDO> args) {
        return ResponseEntity.ok(currentService.describeIngressPage(args));
    }

    @OperationLog
    @ApiOperation("创建Ingress")
    @PostMapping("/createIngress")
    public ResponseEntity<Void> createIngress(@Validated @RequestBody NetworkIngressCreateArgs args) {
        currentService.createIngress(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("删除Ingress")
    @PostMapping("/deleteIngress")
    public ResponseEntity<Void> deleteIngress(@Validated @RequestBody DeleteByIdArgs args) {
        currentService.deleteIngress(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("更新Ingress")
    @PostMapping("/updateIngress")
    public ResponseEntity<Void> updateIngress(@Validated @RequestBody NetworkIngressUpdateArgs args) {
        currentService.updateIngress(args);
        return ResponseEntity.ok().build();
    }
}
