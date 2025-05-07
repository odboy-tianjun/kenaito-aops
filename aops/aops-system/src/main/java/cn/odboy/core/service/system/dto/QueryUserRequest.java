package cn.odboy.core.service.system.dto;

import cn.odboy.base.MyObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class QueryUserRequest extends MyObject {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "多个ID")
    private Set<Long> deptIds = new HashSet<>();

    @ApiModelProperty(value = "模糊查询")
    private String blurry;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "创建时间")
    private List<Timestamp> createTime;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页数据量", example = "10")
    private Integer size = 10;

    @ApiModelProperty(value = "偏移量", hidden = true)
    private long offset;
}
