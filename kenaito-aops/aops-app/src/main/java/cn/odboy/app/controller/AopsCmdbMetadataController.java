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

import cn.hutool.core.lang.Dict;
import cn.odboy.app.constant.AppLanguageEnum;
import cn.odboy.app.constant.AppLevelEnum;
import cn.odboy.app.constant.AppProductLineUserRoleEnum;
import cn.odboy.app.constant.AppUserRoleEnum;
import cn.odboy.app.controller.vo.AopsProductLineVo;
import cn.odboy.app.dal.dataobject.AopsProductLineDO;
import cn.odboy.app.framework.kubernetes.core.constant.KubernetesNetworkTypeEnum;
import cn.odboy.app.framework.kubernetes.core.constant.KubernetesPodStatusEnum;
import cn.odboy.app.service.app.AopsProductLineService;
import cn.odboy.common.constant.GlobalEnableStatusEnum;
import cn.odboy.common.constant.GlobalEnvEnum;
import cn.odboy.common.model.LongMetaOptionItem;
import cn.odboy.common.model.PageArgs;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.model.StringMetaOptionItem;
import cn.odboy.core.service.system.SystemUserService;
import cn.odboy.core.service.system.dto.UserQueryArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/cmdb/metadata")
public class AopsCmdbMetadataController {
    private final AopsProductLineService aopsProductLineService;
    private final SystemUserService systemUserService;

    /**
     * 查询启用状态
     */
    private List<LongMetaOptionItem> getEnableStatusData() {
        return Arrays.stream(GlobalEnableStatusEnum.values())
                .map(m -> LongMetaOptionItem.builder()
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
    private List<StringMetaOptionItem> getEnvData() {
        return Arrays.stream(GlobalEnvEnum.values())
                .map(m -> StringMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询应用等级
     */
    private List<StringMetaOptionItem> getAppLevelData() {
        return Arrays.stream(AppLevelEnum.values())
                .map(m -> StringMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询应用语言
     */
    private List<StringMetaOptionItem> getAppLanguageData() {
        return Arrays.stream(AppLanguageEnum.values())
                .map(m -> StringMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询应用产品线角色
     */
    private List<StringMetaOptionItem> getAppProductLineRoleData() {
        return Arrays.stream(AppProductLineUserRoleEnum.values())
                .map(m -> StringMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询应用角色
     */
    private List<StringMetaOptionItem> getAppRoleData() {
        return Arrays.stream(AppUserRoleEnum.values())
                .map(m -> StringMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询Kubernetes网络类型
     */
    private List<StringMetaOptionItem> getKubernetesNetworkTypeData() {
        return Arrays.stream(KubernetesNetworkTypeEnum.values())
                .map(m -> StringMetaOptionItem.builder()
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
    private List<StringMetaOptionItem> getKubernetesPodStatusData() {
        return Arrays.stream(KubernetesPodStatusEnum.values())
                .map(m -> StringMetaOptionItem.builder()
                        .label(m.getDesc())
                        .value(m.getCode())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private List<LongMetaOptionItem> getProductLineData() {
        PageArgs<AopsProductLineDO> args = new PageArgs<>();
        args.setPage(1);
        args.setSize(Integer.MAX_VALUE);
        PageResult<AopsProductLineVo> pageResult = aopsProductLineService.describeProductLinePage(args);
        List<LongMetaOptionItem> collect = pageResult.getContent().stream()
                .map(m -> LongMetaOptionItem.builder()
                        .label(m.getLineName())
                        .value(m.getId())
                        .build()
                )
                .collect(Collectors.toList());
        collect.add(0, LongMetaOptionItem.builder()
                .label("全部产品线")
                .value(0L)
                .build());
        return collect;
    }

    private List<LongMetaOptionItem> getUserInfoData() {
        return systemUserService.describeUserList(new UserQueryArgs())
                .stream()
                .map(m -> LongMetaOptionItem.builder()
                        .label(m.getNickName())
                        .value(m.getId())
                        .ext(Dict.create()
                                .set("phone", m.getPhone())
                                .set("email", m.getEmail())
                                .set("isAdmin", m.getIsAdmin())
                        )
                        .build())
                .collect(Collectors.toList());
    }

    @ApiOperation("查询所有元数据")
    @PostMapping("/getAll")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("EnableStatus", getEnableStatusData());
        result.put("Env", getEnvData());
        result.put("AppLevel", getAppLevelData());
        result.put("AppLanguage", getAppLanguageData());
        result.put("AppProductLineRole", getAppProductLineRoleData());
        result.put("AppRole", getAppRoleData());
        result.put("KubernetesNetworkType", getKubernetesNetworkTypeData());
        result.put("KubernetesPodStatus", getKubernetesPodStatusData());
        result.put("ProductLine", getProductLineData());
        result.put("UserInfo", getUserInfoData());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
