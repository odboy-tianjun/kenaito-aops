package cn.odboy.core.service.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.List;


@Data
@NoArgsConstructor
public class QueryJobRequest {

    @ApiModelProperty(value = "岗位名称")
    private String name;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "创建时间")
    private List<Timestamp> createTime;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页数据量", example = "10")
    private Integer size = 10;
}