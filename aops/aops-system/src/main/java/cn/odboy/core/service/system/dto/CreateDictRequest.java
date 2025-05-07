package cn.odboy.core.service.system.dto;

import cn.odboy.base.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateDictRequest extends MyObject {
    @NotBlank(message = "字典名称必填")
    private String name;
    private String description;
}
