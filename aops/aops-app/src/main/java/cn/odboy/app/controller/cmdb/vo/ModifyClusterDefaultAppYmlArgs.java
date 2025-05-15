package cn.odboy.app.controller.cmdb.vo;

import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotNull;

/**
 * 仅修改集群默认应用负载配置
 *
 * @author odboy
 * @date 2025-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ModifyClusterDefaultAppYmlArgs extends MyObject {
    @NotNull(message = "ID必填")
    private Long id;
    @NotNull(message = "集群默认应用负载配置必填")
    private String clusterDefaultAppYaml;
}
