package cn.odboy.core.dal.dataobject.system;

import cn.odboy.common.pojo.MyEntity;
import cn.odboy.core.constant.DataScopeEnum;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

/**
 * 角色
 */
@Getter
@Setter
@TableName("system_role")
public class RoleDO extends MyEntity {

    @NotNull(groups = {Update.class})
    @TableId(value = "role_id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户", hidden = true)
    private Set<UserDO> userDOS;

    @TableField(exist = false)
    @ApiModelProperty(value = "菜单", hidden = true)
    private Set<MenuDO> menuDOS;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门", hidden = true)
    private Set<DeptDO> deptDOS;

    @NotBlank
    @ApiModelProperty(value = "名称", hidden = true)
    private String name;

    @ApiModelProperty(value = "数据权限，全部 、 本级 、 自定义")
    private String dataScope = DataScopeEnum.THIS_LEVEL.getValue();

    @ApiModelProperty(value = "级别，数值越小，级别越大")
    private Integer level = 3;

    @ApiModelProperty(value = "描述")
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleDO roleDO = (RoleDO) o;
        return Objects.equals(id, roleDO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
