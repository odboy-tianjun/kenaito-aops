package cn.odboy.base;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import java.lang.reflect.Field;
import java.sql.Timestamp;


@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class MyEntity extends MyObject {

    @CreatedBy
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;

    @LastModifiedBy
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新人", hidden = true)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间: yyyy-MM-dd HH:mm:ss", hidden = true)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间: yyyy-MM-dd HH:mm:ss", hidden = true)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;

    /* 分组校验 */
    public @interface Create {
    }

    /* 分组校验 */
    public @interface Update {
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        Field[] fields = this.getClass().getDeclaredFields();
        try {
            for (Field f : fields) {
                f.setAccessible(true);
                builder.append(f.getName(), f.get(this)).append("\n");
            }
        } catch (Exception e) {
            builder.append("toString builder encounter an error");
        }
        return builder.toString();
    }
}
