package cn.odboy.core.controller.system.vo;

import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserInfoVo extends MyObject {
    private SimpleUserVo user;
    private List<Long> dataScopes;
    private List<RoleCodeVo> authorities;

    public Set<String> getRoles() {
        return authorities.stream().map(RoleCodeVo::getAuthority).collect(Collectors.toSet());
    }
}
