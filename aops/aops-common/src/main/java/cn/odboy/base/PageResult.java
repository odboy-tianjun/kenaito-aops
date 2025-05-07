package cn.odboy.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 分页结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PageResult<T> extends MyObject {
    private List<T> content;
    private long totalElements;
}
