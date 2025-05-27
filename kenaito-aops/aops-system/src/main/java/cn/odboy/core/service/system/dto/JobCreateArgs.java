package cn.odboy.core.service.system.dto;

import cn.odboy.common.model.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class JobCreateArgs extends MyObject {
    @NotBlank(message = "职位名称必填")
    private String name;
    @NotNull(message = "职位排序必填")
    private Integer jobSort;
    private Boolean enabled;
}
