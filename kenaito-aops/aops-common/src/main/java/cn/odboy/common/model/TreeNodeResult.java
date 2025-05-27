package cn.odboy.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TreeNodeResult extends MyObject {
    private Long id;
    private String label;
    private List<TreeNodeResult> children;
    private Long parentId;
    private Boolean ext1;
}
