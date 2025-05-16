package cn.odboy.core.service.system.dto;

import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class DictCreateArgs extends MyObject {
    @NotBlank(message = "字典名称必填")
    private String name;
    private String description;
}
