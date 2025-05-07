package cn.odboy.core.dal.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 修改密码
 */
@Data
public class UpdateUserPasswordResponse {

    @ApiModelProperty(value = "旧密码")
    private String oldPass;

    @ApiModelProperty(value = "新密码")
    private String newPass;
}
