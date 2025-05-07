package cn.odboy.core.service.system.dto;

import cn.odboy.core.dal.dataobject.system.User;
import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserJwtVo implements UserDetails {

    @ApiModelProperty(value = "用户")
    private final User user;

    @ApiModelProperty(value = "数据权限")
    private final List<Long> dataScopes;

    @ApiModelProperty(value = "角色")
    private final List<RoleCodeVo> authorities;

    public Set<String> getRoles() {
        return authorities.stream().map(RoleCodeVo::getAuthority).collect(Collectors.toSet());
    }

    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JSONField(serialize = false)
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    @JSONField(serialize = false)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    public boolean isEnabled() {
        return user.getEnabled();
    }
}
