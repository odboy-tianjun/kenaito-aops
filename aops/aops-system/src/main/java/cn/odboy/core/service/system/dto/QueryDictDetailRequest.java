package cn.odboy.core.service.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryDictDetailRequest {

    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页数据量", example = "10")
    private Integer size = 10;
}