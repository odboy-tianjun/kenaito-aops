package cn.odboy.core.api.system;

import cn.odboy.base.BaseResult;
import cn.odboy.core.dal.dataobject.system.Dept;
import cn.odboy.core.service.system.dto.QueryDeptRequest;
import java.util.List;
import java.util.Set;

public interface SystemDeptApi {

    /**
     * 查询所有数据
     *
     * @param criteria 条件
     * @param isQuery  /
     * @return /
     * @throws Exception /
     */
    List<Dept> describeDeptList(QueryDeptRequest criteria, Boolean isQuery) throws Exception;

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    Dept describeDeptById(Long id);
    /**
     * 根据PID查询
     *
     * @param pid /
     * @return /
     */
    List<Dept> describeDeptListByPid(long pid);

    /**
     * 根据角色ID查询
     *
     * @param id /
     * @return /
     */
    Set<Dept> describeDeptByRoleId(Long id);
    /**
     * 获取部门下所有关联的部门
     *
     * @param deptList /
     * @param depts    /
     * @return /
     */
    Set<Dept> describeRelationDeptSet(List<Dept> deptList, Set<Dept> depts);

    /**
     * 根据ID获取同级与上级数据
     *
     * @param dept  /
     * @param depts /
     * @return /
     */
    List<Dept> describeSuperiorDeptListByPid(Dept dept, List<Dept> depts);

    /**
     * 构建树形数据
     *
     * @param depts /
     * @return /
     */
    BaseResult<Object> buildDeptTree(List<Dept> depts);

    /**
     * 获取
     *
     * @param deptList 、
     * @return 、
     */
    List<Long> describeChildDeptIdListByDeptIds(List<Dept> deptList);

    /**
     * 验证是否被角色或用户关联
     *
     * @param depts /
     */
    void verifyBindRelationByIds(Set<Dept> depts);

    /**
     * 遍历所有部门和子部门
     *
     * @param ids   /
     * @param depts /
     */
    void traverseDeptByIdWithPids(Set<Long> ids, Set<Dept> depts);
}
