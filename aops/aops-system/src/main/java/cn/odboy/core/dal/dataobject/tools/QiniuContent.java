package cn.odboy.core.dal.dataobject.tools;

import cn.odboy.base.MyObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.sql.Timestamp;

/**
 * 上传成功后，存储结果
 */
@Data
@TableName("tool_qiniu_content")
@EqualsAndHashCode(callSuper = false)
public class QiniuContent extends MyObject {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @TableField("name")
    @ApiModelProperty(value = "文件名")
    private String key;

    @ApiModelProperty(value = "空间名")
    private String bucket;

    @ApiModelProperty(value = "大小")
    private String size;

    @ApiModelProperty(value = "文件地址")
    private String url;

    @ApiModelProperty(value = "文件类型")
    private String suffix;

    @ApiModelProperty(value = "空间类型：公开/私有")
    private String type = "公开";

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "创建或更新时间")
    private Timestamp updateTime;
}
