package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.Menu;
import cn.odboy.core.service.system.dto.QueryMenuRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> queryMenuListByArgs(@Param("criteria") QueryMenuRequest criteria);

    LinkedHashSet<Menu> queryMenuSetByRoleIdsAndType(@Param("roleIds") Set<Long> roleIds, @Param("type") Integer type);

    List<Menu> queryMenuListByPidIsNullOrderByMenuSort();

    List<Menu> queryMenuListByPidOrderByMenuSort(@Param("pid") Long pid);

    Menu getMenuByTitle(@Param("title") String title);

    Menu getMenuByComponentName(@Param("name") String name);

    Integer getMenuCountByPid(@Param("pid") Long pid);

    void updateSubCntByMenuId(@Param("count") int count, @Param("menuId") Long menuId);
}
