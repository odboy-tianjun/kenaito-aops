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
@TableName("aops_pipeline_instance")
@ApiModel(value = "AopsPipelineInstance对象", description = "流水线实例")
public class AopsPipelineInstanceDO extends MyEntity {
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("流水线ID")
    @TableField("pipeline_id")
    private Long pipelineId;

    @ApiModelProperty("类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("适用集群")
    @TableField("cluster_code")
    private String clusterCode;

    @ApiModelProperty("应用名称")
    @TableField("app_name")
    private String appName;

    @ApiModelProperty("状态(初始化init、运行中running、执行成功success、执行失败error)")
    @TableField("instance_status")
    private String instanceStatus;

    @ApiModelProperty("当前节点业务编码")
    @TableField("current_node_code")
    private String currentNodeCode;

    @ApiModelProperty("当前节点业务状态(初始化init、运行中running、执行成功success、执行失败error)")
    @TableField("current_node_status")
    private String currentNodeStatus;

    @ApiModelProperty("实例参数")
    @TableField("instance_params")
    private String instanceParams;

    @ApiModelProperty("实例模版内容")
    @TableField("instance_template")
    private String instanceTemplate;

    @ApiModelProperty("实例执行记录")
    @TableField("instance_execute_log")
    private String instanceExecuteLog;

    @ApiModelProperty("发布分支")
    @TableField("release_branch")
    private String releaseBranch;
}
