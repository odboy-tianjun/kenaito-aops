package cn.odboy.core.dal.model;

import cn.odboy.base.MyObject;
import cn.odboy.core.service.system.dto.RoleCodeVo;
import cn.odboy.core.service.system.dto.SimpleUserVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserInfoResponse extends MyObject {
    private SimpleUserVo user;
    private List<Long> dataScopes;
    private List<RoleCodeVo> authorities;

    public Set<String> getRoles() {
        return authorities.stream().map(RoleCodeVo::getAuthority).collect(Collectors.toSet());
    }
}
