package cn.odboy.core.dal.dataobject.system;

import cn.odboy.common.pojo.MyEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;


@Getter
@Setter
@TableName("system_job")
public class JobDO extends MyEntity {

    @NotNull(groups = Update.class)
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "岗位名称")
    private String name;

    @NotNull
    @ApiModelProperty(value = "岗位排序")
    private Long jobSort;

    @NotNull
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobDO jobDO = (JobDO) o;
        return Objects.equals(id, jobDO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}