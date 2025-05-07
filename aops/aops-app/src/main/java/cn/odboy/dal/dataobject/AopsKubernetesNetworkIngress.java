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
 * Kubernetes网络ingress-nginx
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Getter
@Setter
@ToString
@TableName("aops_kubernetes_network_ingress")
@ApiModel(value = "AopsKubernetesNetworkIngress对象", description = "Kubernetes网络ingress-nginx")
public class AopsKubernetesNetworkIngress extends MyLogicEntity {

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
     * 应用名称
     */
    @TableField("app_name")
    @ApiModelProperty("应用名称")
    private String appName;

    /**
     * ingress名称
     */
    @TableField("ingress_name")
    @ApiModelProperty("ingress名称")
    private String ingressName;

    /**
     * 绑定域名
     */
    @TableField("hostname")
    @ApiModelProperty("绑定域名")
    private String hostname;

    /**
     * 绑定域名的类型，内网或者外网
     */
    @TableField("network_type")
    @ApiModelProperty("绑定域名的类型，内网或者外网")
    private String networkType;

    /**
     * 绑定的ServiceId
     */
    @TableField("service_id")
    @ApiModelProperty("绑定的ServiceId")
    private Long serviceId;

    /**
     * 绑定的路径，默认为\"/\"
     */
    @TableField("`path`")
    @ApiModelProperty("绑定的路径，默认为\"/\"")
    private String path;
}
