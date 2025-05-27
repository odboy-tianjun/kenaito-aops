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

import cn.odboy.common.model.MyLogicEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@TableName("aops_product_line")
@ApiModel(value = "AopsProductLine对象", description = "产品线")
public class AopsProductLineDO extends MyLogicEntity {
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("父产品")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty("产品名称")
    @TableField("line_name")
    private String lineName;

    @ApiModelProperty("产品负责人")
    @TableField("owner")
    private String owner;

    @ApiModelProperty("产品运维")
    @TableField("pe")
    private String pe;

    @TableField("remark")
    @ApiModelProperty("描述")
    private String remark;
}
