package cn.odboy.app.framework.gitlab.model;

import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建Gitlab项目
 *
 * @author odboy
 * @date 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GitlabProjectCreateArgs extends MyObject {
    /**
     * 项目中文名称
     */
    private String name;
    /**
     * 项目英文名称
     */
    @NotBlank(message = "应用名称不能为空")
    private String appName;
    @NotNull(message = "GitGroup不能为空")
    private Long groupOrUserId;
    /**
     * 项目描述
     */
    private String description;
}
