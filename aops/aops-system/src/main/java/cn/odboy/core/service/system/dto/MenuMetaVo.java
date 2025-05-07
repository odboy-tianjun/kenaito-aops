package cn.odboy.core.service.system.dto;

import cn.odboy.base.MyObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MenuMetaVo extends MyObject {

    @ApiModelProperty(value = "菜单标题")
    private String title;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "缓存")
    private Boolean noCache;
}
