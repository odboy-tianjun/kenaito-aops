package cn.odboy.core.service.system.dto;

import cn.odboy.base.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateDeptRequest extends MyObject {
    @NotBlank(message = "部门名称必填")
    private String name;
    @NotNull(message = "部门排序必填")
    private Integer deptSort;
    private Boolean enabled;
    private String isTop;
    private Long pid;
    private Integer subCount = 0;
}
