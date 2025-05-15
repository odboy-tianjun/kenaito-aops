package cn.odboy.common.pojo.vo;

import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class RemoveByIdArgs extends MyObject {
    @NotNull(message = "ID必填")
    private Long id;
}
