package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Set;

@Mapper
public interface RoleDeptMapper {
    void insertBatchWithRoleId(@Param("depts") Set<Dept> depts, @Param("roleId") Long roleId);

    void deleteByRoleId(@Param("roleId") Long roleId);

    void deleteByRoleIds(@Param("roleIds") Set<Long> roleIds);
}
