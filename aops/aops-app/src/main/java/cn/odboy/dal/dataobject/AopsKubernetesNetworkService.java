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
 * Kubernetes网络service
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Getter
@Setter
@ToString
@TableName("aops_kubernetes_network_service")
@ApiModel(value = "AopsKubernetesNetworkService对象", description = "Kubernetes网络service")
public class AopsKubernetesNetworkService extends MyLogicEntity {

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
     * 应用、资源名称
     */
    @TableField("app_name")
    @ApiModelProperty("应用、资源名称")
    private String appName;

    /**
     * 服务类型(应用 app 资源 resource)
     */
    @TableField("service_type")
    @ApiModelProperty("服务类型(应用 app 资源 resource)")
    private String serviceType;

    /**
     * 服务名称
     */
    @ApiModelProperty("服务名称")
    @TableField("service_name")
    private String serviceName;

    /**
     * 服务端口
     */
    @ApiModelProperty("服务端口")
    @TableField("service_port")
    private Integer servicePort;

    /**
     * 容器端口
     */
    @ApiModelProperty("容器端口")
    @TableField("service_target_port")
    private Integer serviceTargetPort;
}
