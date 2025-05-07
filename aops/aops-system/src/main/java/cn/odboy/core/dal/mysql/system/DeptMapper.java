package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.service.system.dto.QueryDeptRequest;
import cn.odboy.core.dal.dataobject.system.Dept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Set;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {
    List<Dept> queryDeptListByArgs(@Param("criteria") QueryDeptRequest criteria);

    List<Dept> queryDeptListByPid(@Param("pid") Long pid);

    int getDeptCountByPid(@Param("pid") Long pid);

    List<Dept> queryDeptListByPidIsNull();

    Set<Dept> queryDeptSetByRoleId(@Param("roleId") Long roleId);

    void updateSubCountById(@Param("count") Integer count, @Param("id") Long id);
}