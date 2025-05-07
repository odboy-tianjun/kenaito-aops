package cn.odboy.core.service.tools.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;


@Data
public class QueryQiniuRequest {

    @ApiModelProperty(value = "名称查询")
    private String key;

    @ApiModelProperty(value = "创建时间")
    private List<Timestamp> createTime;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页数据量", example = "10")
    private Integer size = 10;
}
