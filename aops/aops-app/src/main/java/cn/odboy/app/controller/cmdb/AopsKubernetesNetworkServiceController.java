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

import cn.odboy.app.controller.cmdb.vo.NetworkServiceCreateArgs;
import cn.odboy.app.controller.cmdb.vo.NetworkServiceUpdateArgs;
import cn.odboy.app.dal.dataobject.AopsKubernetesNetworkServiceDO;
import cn.odboy.app.service.kubernetes.AopsKubernetesNetworkServiceService;
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

@Api(tags = "Kubernetes集群Service")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cmdb/kubernetes/network")
public class AopsKubernetesNetworkServiceController {
    private final AopsKubernetesNetworkServiceService currentService;

    @ApiOperation("分页查询Service列表")
    @PostMapping("/describeServicePage")
    @PreAuthorize("@el.check()")
    public ResponseEntity<PageResult<AopsKubernetesNetworkServiceDO>> describeServicePage(@Validated @RequestBody PageArgs<AopsKubernetesNetworkServiceDO> args) {
        return ResponseEntity.ok(currentService.describeServicePage(args));
    }

    @OperationLog
    @ApiOperation("创建Service")
    @PostMapping("/createService")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Void> createService(@Validated @RequestBody NetworkServiceCreateArgs args) {
        currentService.createService(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("删除Service")
    @PostMapping("/deleteService")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Void> deleteService(@Validated @RequestBody DeleteByIdArgs args) {
        currentService.deleteService(args);
        return ResponseEntity.ok().build();
    }

    @OperationLog
    @ApiOperation("更新Service")
    @PostMapping("/updateService")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Void> updateService(@Validated @RequestBody NetworkServiceUpdateArgs args) {
        currentService.updateService(args);
        return ResponseEntity.ok().build();
    }
}
