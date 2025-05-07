package cn.odboy.core.service.system.dto;

import cn.odboy.base.MyObject;
import cn.odboy.core.dal.dataobject.system.Dept;
import cn.odboy.core.dal.dataobject.system.Job;
import cn.odboy.core.dal.dataobject.system.Role;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class SimpleUserVo extends MyObject {
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;
    @ApiModelProperty(value = "用户角色")
    private Set<Role> roles;
    @ApiModelProperty(value = "用户岗位")
    private Set<Job> jobs;
    private Long deptId;
    @TableField(exist = false)
    private Dept dept;
    @ApiModelProperty(value = "用户名称")
    private String username;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "电话号码")
    private String phone;
    @ApiModelProperty(value = "用户性别")
    private String gender;
    @ApiModelProperty(value = "头像真实名称", hidden = true)
    private String avatarName;
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;
    @ApiModelProperty(value = "是否为admin账号", hidden = true)
    private Boolean isAdmin = false;
}