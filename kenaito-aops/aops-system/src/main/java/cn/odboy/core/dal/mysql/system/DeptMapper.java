package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.dal.dataobject.system.DeptDO;
import cn.odboy.core.service.system.dto.DeptQueryArgs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Set;

@Mapper
public interface DeptMapper extends BaseMapper<DeptDO> {
    List<DeptDO> queryDeptListByArgs(@Param("args") DeptQueryArgs args);

    List<DeptDO> queryDeptListByPid(@Param("pid") Long pid);

    int getDeptCountByPid(@Param("pid") Long pid);

    List<DeptDO> queryDeptListByPidIsNull();

    Set<DeptDO> queryDeptSetByRoleId(@Param("roleId") Long roleId);

    void updateSubCountById(@Param("count") Integer count, @Param("id") Long id);
}
