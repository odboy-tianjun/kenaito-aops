/*
 *  Copyright 2021-2025 Tian Jun
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
package cn.odboy.dal.dataobject;

import cn.odboy.base.MyLogicEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * Kubernetes集群node节点
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Getter
@Setter
@ToString
@TableName("aops_kubernetes_cluster_node")
@ApiModel(value = "AopsKubernetesClusterNode对象", description = "Kubernetes集群node节点")
public class AopsKubernetesClusterNode extends MyLogicEntity {

    /**
     * 自增ID
     */
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 集群配置id
     */
    @ApiModelProperty("集群配置id")
    @TableField("cluster_config_id")
    private Long clusterConfigId;

    /**
     * 环境编码
     */
    @TableField("env_code")
    @ApiModelProperty("环境编码")
    private String envCode;

    /**
     * 节点名称
     */
    @TableField("node_name")
    @ApiModelProperty("节点名称")
    private String nodeName;

    /**
     * 节点状态
     */
    @ApiModelProperty("节点状态")
    @TableField("node_status")
    private String nodeStatus;

    /**
     * 节点所属角色列表
     */
    @TableField("node_roles")
    @ApiModelProperty("节点所属角色列表")
    private String nodeRoles;

    /**
     * 节点存活时间
     */
    @TableField("node_age")
    @ApiModelProperty("节点存活时间")
    private String nodeAge;

    /**
     * 节点上k8s工具版本
     */
    @ApiModelProperty("节点上k8s工具版本")
    @TableField("node_k8s_version")
    private String nodeK8sVersion;

    /**
     * 节点内部ip
     */
    @ApiModelProperty("节点内部ip")
    @TableField("node_internal_ip")
    private String nodeInternalIp;

    /**
     * 节点主机名称
     */
    @ApiModelProperty("节点主机名称")
    @TableField("node_hostname")
    private String nodeHostname;

    /**
     * 节点上所使用系统名称
     */
    @TableField("node_os_image")
    @ApiModelProperty("节点上所使用系统名称")
    private String nodeOsImage;

    /**
     * 节点上所使用系统内核版本
     */
    @ApiModelProperty("节点上所使用系统内核版本")
    @TableField("node_os_kernel_version")
    private String nodeOsKernelVersion;

    /**
     * 节点上所使用系统架构
     */
    @ApiModelProperty("节点上所使用系统架构")
    @TableField("node_os_architecture")
    private String nodeOsArchitecture;

    /**
     * 节点上所使用运行时
     */
    @ApiModelProperty("节点上所使用运行时")
    @TableField("node_container_runtime")
    private String nodeContainerRuntime;

    /**
     * 节点上pod的cidr
     */
    @TableField("node_pod_cidr")
    @ApiModelProperty("节点上pod的cidr")
    private String nodePodCidr;

    /**
     * 节点上pod的数量
     */
    @TableField("node_pod_size")
    @ApiModelProperty("节点上pod的数量")
    private Integer nodePodSize;
}
