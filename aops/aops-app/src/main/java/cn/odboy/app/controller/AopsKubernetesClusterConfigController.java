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

import cn.odboy.app.dal.dataobject.AopsKubernetesClusterConfigDO;
import cn.odboy.app.service.kubernetes.AopsKubernetesClusterConfigService;
import cn.odboy.common.pojo.PageArgs;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.dal.dataobject.system.DictDO;
import cn.odboy.core.service.system.dto.QueryDictArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    @ApiOperation("查询集群配置")
    @PostMapping("describeKubernetesClusterConfigPage")
    @PreAuthorize("@el.check('aops:kubernetes:clusterConfig:list')")
    public ResponseEntity<PageResult<AopsKubernetesClusterConfigDO>> queryDict(PageArgs<AopsKubernetesClusterConfigDO> args) {
        return new ResponseEntity<>(aopsKubernetesClusterConfigService.describeKubernetesClusterConfigPage(args), HttpStatus.OK);
    }
}
