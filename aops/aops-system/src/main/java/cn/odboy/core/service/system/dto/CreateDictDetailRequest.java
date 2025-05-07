package cn.odboy.core.service.system.dto;

import cn.odboy.base.MyObject;
import cn.odboy.core.dal.dataobject.system.Dict;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateDictDetailRequest extends MyObject {
    @NotBlank(message = "参数dict必填")
    private Dict dict;
    @NotBlank(message = "字典标签必填")
    private String label;
    @NotBlank(message = "字典值必填")
    private String value;
    @NotNull(message = "字典排序必填")
    private Integer dictSort;
}
