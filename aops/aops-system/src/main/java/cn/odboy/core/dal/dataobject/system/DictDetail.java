package cn.odboy.core.dal.dataobject.system;

import cn.odboy.base.MyEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@TableName("system_dict_detail")
public class DictDetail extends MyEntity {

    @NotNull(groups = Update.class)
    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "dict_id")
    @ApiModelProperty(hidden = true)
    private Long dictId;

    @TableField(exist = false)
    @ApiModelProperty(value = "字典")
    private Dict dict;

    @ApiModelProperty(value = "字典标签")
    private String label;

    @ApiModelProperty(value = "字典值")
    private String value;

    @ApiModelProperty(value = "排序")
    private Integer dictSort = 999;
}