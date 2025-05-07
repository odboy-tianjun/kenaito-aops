package cn.odboy.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 数据分页参数
 *
 * @author odboy
 * @date 2025-01-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageArgs<T> implements Serializable {
    @NotNull(message = "参数page不能为空")
    @Min(value = 1, message = "参数page最小值为1")
    @ApiModelProperty(value = "页码", example = "1")
    private Integer page;
    @NotNull(message = "参数size不能为空")
    @Min(value = 1, message = "参数size最小值为1")
    @ApiModelProperty(value = "每页数据量", example = "10")
    private Integer size;
    private T args;
}
