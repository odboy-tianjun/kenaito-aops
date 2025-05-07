package cn.odboy.core.dal.dataobject.tools;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.odboy.base.MyEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@TableName("tool_local_storage")
public class LocalStorage extends MyEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "真实文件名")
    private String realName;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "后缀")
    private String suffix;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "大小")
    private String size;

    public LocalStorage(String realName, String name, String suffix, String path, String type, String size) {
        this.realName = realName;
        this.name = name;
        this.suffix = suffix;
        this.path = path;
        this.type = type;
        this.size = size;
    }

    public void copy(LocalStorage source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}