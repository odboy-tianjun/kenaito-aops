package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.DeptDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Set;

@Mapper
public interface RoleDeptMapper {
    void insertBatchWithRoleId(@Param("deptDOS") Set<DeptDO> deptDOS, @Param("roleId") Long roleId);

    void deleteByRoleId(@Param("roleId") Long roleId);

    void deleteByRoleIds(@Param("roleIds") Set<Long> roleIds);
}
