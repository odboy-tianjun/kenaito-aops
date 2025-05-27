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
@TableName("aops_kubernetes_containerd_spec_config")
@ApiModel(value = "AopsKubernetesContainerdSpecConfig对象", description = "Kubernetes容器规格配置")
public class AopsKubernetesContainerdSpecConfigDO extends MyLogicEntity {


    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("spec_name")
    @ApiModelProperty("规格名称")
    private String specName;


    @TableField("cpu_num")
    @ApiModelProperty("cpu核数")
    private Integer cpuNum;

    @TableField("mem_num")
    @ApiModelProperty("内存大小，单位Gi")
    private Integer memNum;

    @TableField("disk_size")
    @ApiModelProperty("磁盘大小，单位Gi")
    private Integer diskSize;
}
