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

import cn.odboy.common.model.MyEntity;
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
@TableName("aops_pipeline_template")
@ApiModel(value = "AopsPipelineTemplate对象", description = "流水线模版")
public class AopsPipelineTemplateDO extends MyEntity {
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("语言")
    @TableField("language")
    private String language;

    @ApiModelProperty("模版编码")
    @TableField("template_code")
    private String templateCode;

    @ApiModelProperty("名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("模版内容")
    @TableField("feature")
    private String feature;

    @ApiModelProperty("适用集群")
    @TableField("cluster_code")
    private String clusterCode;
}
