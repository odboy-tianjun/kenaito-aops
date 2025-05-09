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
package cn.odboy.app.dal.dataobject;

import cn.odboy.common.pojo.MyObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 应用、成员关联
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Getter
@Setter
@ToString
@TableName("aops_app_user")
@ApiModel(value = "AopsAppUser对象", description = "应用、成员关联")
public class AopsAppUserDO extends MyObject {

    /**
     * 应用id
     */
    @ApiModelProperty("应用id")
    @TableField(value = "app_id")
    private Long appId;

    /**
     * 用户Id
     */
    @ApiModelProperty("用户Id")
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 角色编码
     */
    @ApiModelProperty("角色编码")
    @TableField(value = "role_code")
    private String roleCode;
}
