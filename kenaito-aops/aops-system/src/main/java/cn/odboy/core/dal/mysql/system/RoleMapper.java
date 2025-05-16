package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.RoleDO;
import cn.odboy.core.service.system.dto.RoleQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Set;

@Mapper
public interface RoleMapper extends BaseMapper<RoleDO> {
    List<RoleDO> queryRoleList();

    List<RoleDO> queryRoleListByArgs(@Param("args") RoleQueryArgs args);

    Long getRoleCountByArgs(@Param("args") RoleQueryArgs args);

    RoleDO getRoleByName(@Param("name") String name);

    RoleDO getRoleById(@Param("roleId") Long roleId);

    List<RoleDO> queryRoleListByUserId(@Param("userId") Long userId);

    int getRoleCountByDeptIds(@Param("deptIds") Set<Long> deptIds);

    List<RoleDO> queryRoleListByMenuId(@Param("menuId") Long menuId);
}
