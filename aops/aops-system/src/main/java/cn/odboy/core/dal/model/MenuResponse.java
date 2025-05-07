package cn.odboy.core.dal.model;

import cn.odboy.base.MyObject;
import cn.odboy.core.service.system.dto.MenuMetaVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * 构建前端路由时用到
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MenuResponse extends MyObject {

    @ApiModelProperty(value = "菜单名称")
    private String name;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "隐藏状态")
    private Boolean hidden;

    @ApiModelProperty(value = "重定向")
    private String redirect;

    @ApiModelProperty(value = "组件")
    private String component;

    @ApiModelProperty(value = "总是显示")
    private Boolean alwaysShow;

    @ApiModelProperty(value = "元数据")
    private MenuMetaVo meta;

    @ApiModelProperty(value = "子路由")
    private List<MenuResponse> children;
}
