package cn.odboy.core.service.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;


@Data
public class QueryMenuRequest {

    @ApiModelProperty(value = "模糊查询")
    private String blurry;

    @ApiModelProperty(value = "创建时间")
    private List<Timestamp> createTime;

    @ApiModelProperty(value = "PID为空查询")
    private Boolean pidIsNull;

    @ApiModelProperty(value = "PID")
    private Long pid;
}
