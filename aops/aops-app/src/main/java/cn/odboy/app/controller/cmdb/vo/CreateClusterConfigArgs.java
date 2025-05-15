package cn.odboy.app.controller.cmdb.vo;

import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = false)
public class CreateClusterConfigArgs extends MyObject {
    @NotBlank(message = "环境编码必填")
    private String envCode;
    @NotBlank(message = "集群编码必填")
    private String clusterCode;
    @NotBlank(message = "集群名称必填")
    private String clusterName;
    @NotBlank(message = "集群配置必填")
    private String clusterConfigContent;
    @NotBlank(message = "集群默认应用镜像必填")
    private String clusterDefaultAppImage;
}
