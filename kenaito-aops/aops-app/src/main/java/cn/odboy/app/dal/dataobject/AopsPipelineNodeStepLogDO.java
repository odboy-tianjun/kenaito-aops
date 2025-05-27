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
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;


@Getter
@Setter
@ToString
@TableName("aops_pipeline_node_step_log")
@ApiModel(value = "AopsPipelineNodeStepLog对象", description = "流水线节点步骤日志")
public class AopsPipelineNodeStepLogDO extends MyEntity {
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("节点业务编码")
    @TableField("node_code")
    private String nodeCode;

    @ApiModelProperty("节点业务标题")
    @TableField("node_title")
    private String nodeTitle;

    @ApiModelProperty("节点业务结果")
    @TableField("node_result")
    private String nodeResult;

    @TableField
    @ApiModelProperty(value = "开始时间: yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @TableField
    @ApiModelProperty(value = "结束时间: yyyy-MM-dd HH:mm:ss")
    private Date finishTime;
}
