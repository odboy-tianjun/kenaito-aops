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

import cn.odboy.common.pojo.MyLogicEntity;
import cn.odboy.common.pojo.MyObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * Kubernetes集群配置
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Getter
@Setter
@ToString
@TableName("aops_kubernetes_cluster_config")
@ApiModel(value = "AopsKubernetesClusterConfig对象", description = "Kubernetes集群配置")
public class AopsKubernetesClusterConfigDO extends MyLogicEntity {

    /**
     * 自增ID
     */
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 环境编码
     */
    @TableField("env_code")
    @ApiModelProperty("环境编码")
    private String envCode;

    /**
     * 集群编码
     */
    @TableField("cluster_code")
    @ApiModelProperty("集群编码")
    private String clusterCode;

    /**
     * 集群名称
     */
    @TableField("cluster_name")
    @ApiModelProperty("集群名称")
    private String clusterName;

    /**
     * 配置内容
     */
    @ApiModelProperty("配置内容")
    @TableField("config_content")
    private String configContent;

    /**
     * 健康状态(1、健康 2、不健康)
     */
    @TableField("`status`")
    @ApiModelProperty("健康状态(1、健康 2、不健康)")
    private Integer status;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class QueryPage extends MyObject {
        private Long id;
        private String envCode;
        private String clusterCode;
        private String clusterName;
        private String configContent;
        private Integer envStatus;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateArgs extends MyObject {
        @NotBlank(message = "必填")
        private String envCode;
        @NotBlank(message = "必填")
        private String clusterCode;
        @NotBlank(message = "必填")
        private String clusterName;
        @NotBlank(message = "必填")
        private String configContent;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ModifyArgs extends MyObject {
        @NotNull(message = "必填")
        private Long id;
        @NotBlank(message = "必填")
        private String configContent;
    }
}
