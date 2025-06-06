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
@TableName("aops_kubernetes_cluster_config")
@ApiModel(value = "AopsKubernetesClusterConfig对象", description = "Kubernetes集群配置")
public class AopsKubernetesClusterConfigDO extends MyLogicEntity {

    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("env_code")
    @ApiModelProperty("环境编码")
    private String envCode;

    @TableField("cluster_code")
    @ApiModelProperty("集群编码")
    private String clusterCode;

    @TableField("cluster_name")
    @ApiModelProperty("集群名称")
    private String clusterName;

    @ApiModelProperty("集群配置内容")
    @TableField("cluster_config_content")
    private String clusterConfigContent;

    @TableField("`status`")
    @ApiModelProperty("是否启用(1、启用 0、禁用)")
    private String status;

    @TableField("cluster_default_app_image")
    @ApiModelProperty("集群默认应用镜像")
    private String clusterDefaultAppImage;

    @TableField("cluster_default_app_yaml")
    @ApiModelProperty("集群默认应用yaml")
    private String clusterDefaultAppYaml;

    @TableField("cluster_node_size")
    @ApiModelProperty("集群节点数量")
    private Integer clusterNodeSize;

    @TableField("cluster_pod_size")
    @ApiModelProperty("集群Pod数量")
    private Integer clusterPodSize;
}
