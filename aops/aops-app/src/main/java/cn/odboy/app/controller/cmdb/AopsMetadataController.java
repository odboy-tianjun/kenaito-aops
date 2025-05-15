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

import cn.hutool.core.lang.Dict;
import cn.odboy.app.constant.AppLanguageEnum;
import cn.odboy.app.constant.AppLevelEnum;
import cn.odboy.app.constant.AppProductLineUserRoleEnum;
import cn.odboy.app.constant.AppUserRoleEnum;
import cn.odboy.app.framework.kubernetes.core.constant.KubernetesNetworkTypeEnum;
import cn.odboy.app.framework.kubernetes.core.constant.KubernetesPodStatusEnum;
import cn.odboy.common.constant.GlobalEnableStatusEnum;
import cn.odboy.common.constant.GlobalEnvEnum;
import cn.odboy.common.pojo.MyMetaOption;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Cmdb 元数据
 *
 * @author odboy
 * @date 2025-05-15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aops/cmdb/metadata")
public class AopsMetadataController {
    @ApiOperation("查询启用状态元数据")
    @PostMapping("/describeEnableStatusMetadataOptions")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> describeEnableStatusMetadataOptions() {
        return new ResponseEntity<>(Arrays.stream(GlobalEnableStatusEnum.values())
                .map(m -> MyMetaOption.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation("查询环境元数据")
    @PostMapping("/describeEnvMetadataOptions")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> describeEnvMetadataOptions() {
        return new ResponseEntity<>(Arrays.stream(GlobalEnvEnum.values())
                .map(m -> MyMetaOption.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation("查询应用级别元数据")
    @PostMapping("/describeAppLevelMetadataOptions")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> describeAppLevelMetadataOptions() {
        return new ResponseEntity<>(Arrays.stream(AppLevelEnum.values())
                .map(m -> MyMetaOption.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation("查询应用语言元数据")
    @PostMapping("/describeAppLanguageMetadataOptions")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> describeAppLanguageMetadataOptions() {
        return new ResponseEntity<>(Arrays.stream(AppLanguageEnum.values())
                .map(m -> MyMetaOption.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation("查询应用产品线角色元数据")
    @PostMapping("/describeAppProductLineRoleMetadataOptions")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> describeAppProductLineRoleMetadataOptions() {
        return new ResponseEntity<>(Arrays.stream(AppProductLineUserRoleEnum.values())
                .map(m -> MyMetaOption.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation("查询应用角色元数据")
    @PostMapping("/describeAppRoleMetadataOptions")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> describeAppRoleMetadataOptions() {
        return new ResponseEntity<>(Arrays.stream(AppUserRoleEnum.values())
                .map(m -> MyMetaOption.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation("查询Kubernetes网络类型元数据")
    @PostMapping("/describeKubernetesNetworkTypeMetadataOptions")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> describeKubernetesNetworkTypeMetadataOptions() {
        return new ResponseEntity<>(Arrays.stream(KubernetesNetworkTypeEnum.values())
                .map(m -> MyMetaOption.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .ext(Dict.create().set("suffix", m.getSuffix()))
                        .build()
                )
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation("查询KubernetesPod状态元数据")
    @PostMapping("/describeKubernetesPodStatusMetadataOptions")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> describeKubernetesPodStatusMetadataOptions() {
        return new ResponseEntity<>(Arrays.stream(KubernetesPodStatusEnum.values())
                .map(m -> MyMetaOption.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList()), HttpStatus.OK);
    }
}
