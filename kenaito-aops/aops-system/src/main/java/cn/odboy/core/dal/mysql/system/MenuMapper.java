package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.MenuDO;
import cn.odboy.core.service.system.dto.MenuQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mapper
public interface MenuMapper extends BaseMapper<MenuDO> {

    List<MenuDO> queryMenuListByArgs(@Param("args") MenuQueryArgs args);

    LinkedHashSet<MenuDO> queryMenuSetByRoleIdsAndType(@Param("roleIds") Set<Long> roleIds, @Param("type") Integer type);

    List<MenuDO> queryMenuListByPidIsNullOrderByMenuSort();

    List<MenuDO> queryMenuListByPidOrderByMenuSort(@Param("pid") Long pid);

    MenuDO getMenuByTitle(@Param("title") String title);

    MenuDO getMenuByComponentName(@Param("name") String name);

    Integer getMenuCountByPid(@Param("pid") Long pid);

    void updateSubCntByMenuId(@Param("count") int count, @Param("menuId") Long menuId);
}
