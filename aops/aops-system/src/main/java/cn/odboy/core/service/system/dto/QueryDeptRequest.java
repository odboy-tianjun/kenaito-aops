package cn.odboy.core.service.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
public class QueryDeptRequest {

    @ApiModelProperty(value = "部门id集合")
    private List<Long> ids;

    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "上级部门")
    private Long pid;

    @ApiModelProperty(value = "PID为空查询")
    private Boolean pidIsNull;

    @ApiModelProperty(value = "创建时间")
    private List<Timestamp> createTime;
}