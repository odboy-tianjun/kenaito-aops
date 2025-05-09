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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * Kubernetes容器规格配置
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Getter
@Setter
@ToString
@TableName("aops_kubernetes_containerd_spec_config")
@ApiModel(value = "AopsKubernetesContainerdSpecConfig对象", description = "Kubernetes容器规格配置")
public class AopsKubernetesContainerdSpecConfig extends MyLogicEntity {

    /**
     * 自增ID
     */
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规格名称
     */
    @TableField("spec_name")
    @ApiModelProperty("规格名称")
    private String specName;

    /**
     * cpu核数
     */
    @TableField("cpu_num")
    @ApiModelProperty("cpu核数")
    private Integer cpuNum;

    /**
     * 内存大小，单位Gi
     */
    @TableField("mem_num")
    @ApiModelProperty("内存大小，单位Gi")
    private Integer memNum;

    /**
     * 磁盘大小，单位Gi
     */
    @TableField("disk_size")
    @ApiModelProperty("磁盘大小，单位Gi")
    private Integer diskSize;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class QueryPage extends MyObject {
        private Long id;
        private String specName;
        private Integer cpuNum;
        private Integer memNum;
        private Integer diskSize;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateArgs extends MyObject {
        @NotBlank(message = "必填")
        private String specName;
        @NotNull(message = "必填")
        @Min(message = "最小值为1", value = 1)
        @Max(message = "最大值为256", value = 256)
        private Integer cpuNum;
        @NotNull(message = "必填")
        @Min(message = "最小值为1", value = 1)
        @Max(message = "最大值为512", value = 512)
        private Integer memNum;
        @NotNull(message = "必填")
        @Min(message = "最小值为60", value = 60)
        @Max(message = "最大值为180", value = 180)
        private Integer diskSize;
    }
}
