package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.MenuDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Set;


@Mapper
public interface RoleMenuMapper {
    void insertBatchWithRoleId(@Param("menuDOS") Set<MenuDO> menuDOS, @Param("roleId") Long roleId);

    void deleteByRoleId(@Param("roleId") Long roleId);

    void deleteByRoleIds(@Param("roleIds") Set<Long> roleIds);

    void deleteByMenuId(@Param("menuId") Long menuId);
}
