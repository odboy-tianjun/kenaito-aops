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
import cn.odboy.common.pojo.MyMetaOptionItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "CMDB元数据管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aops/cmdb/metadata")
public class AopsCmdbMetadataController {
    /**
     * 查询启用状态
     */
    private List<MyMetaOptionItem> getEnableStatusData() {
        return Arrays.stream(GlobalEnableStatusEnum.values())
                .map(m -> MyMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .ext(Dict.create().set("tagType", m.getTagType()))
                        .build()
                )
                .collect(Collectors.toList());
    }
    /**
     * 查询环境编码
     */
    private List<MyMetaOptionItem> getEnvData() {
        return Arrays.stream(GlobalEnvEnum.values())
                .map(m -> MyMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }
    /**
     * 查询应用等级
     */
    private List<MyMetaOptionItem> getAppLevelData() {
        return Arrays.stream(AppLevelEnum.values())
                .map(m -> MyMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询应用语言
     */
    private List<MyMetaOptionItem> getAppLanguageData() {
        return Arrays.stream(AppLanguageEnum.values())
                .map(m -> MyMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询应用产品线角色
     */
    private List<MyMetaOptionItem> getAppProductLineRoleData() {
        return Arrays.stream(AppProductLineUserRoleEnum.values())
                .map(m -> MyMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询应用角色
     */
    private List<MyMetaOptionItem> getAppRoleData() {
        return Arrays.stream(AppUserRoleEnum.values())
                .map(m -> MyMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询Kubernetes网络类型
     */
    private List<MyMetaOptionItem> getKubernetesNetworkTypeData() {
        return Arrays.stream(KubernetesNetworkTypeEnum.values())
                .map(m -> MyMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .ext(Dict.create().set("suffix", m.getSuffix()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询KubernetesPod状态
     */
    private List<MyMetaOptionItem> getKubernetesPodStatusData() {
        return Arrays.stream(KubernetesPodStatusEnum.values())
                .map(m -> MyMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }


    @ApiOperation("查询所有元数据")
    @PostMapping("/getAll")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> getAll() {
        Map<String, List<MyMetaOptionItem>> result = new LinkedHashMap<>();
        result.put("EnableStatus", getEnableStatusData());
        result.put("Env", getEnvData());
        result.put("AppLevel", getAppLevelData());
        result.put("AppLanguage", getAppLanguageData());
        result.put("AppProductLineRole", getAppProductLineRoleData());
        result.put("AppRole", getAppRoleData());
        result.put("KubernetesNetworkType", getKubernetesNetworkTypeData());
        result.put("KubernetesPodStatus", getKubernetesPodStatusData());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
