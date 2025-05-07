package cn.odboy.core.service.tools.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 发送邮件时，接收参数的类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRequest {

    @NotEmpty
    @ApiModelProperty(value = "收件人")
    private List<String> tos;

    @NotBlank
    @ApiModelProperty(value = "主题")
    private String subject;

    @NotBlank
    @ApiModelProperty(value = "内容")
    private String content;
}
