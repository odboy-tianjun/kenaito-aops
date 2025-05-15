package cn.odboy.core.service.system;

import cn.odboy.common.pojo.BaseResult;
import cn.odboy.core.dal.dataobject.system.DeptDO;
import cn.odboy.core.service.system.dto.CreateDeptArgs;
import cn.odboy.core.service.system.dto.QueryDeptArgs;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface SystemDeptService extends IService<DeptDO> {
    /**
     * 查询所有数据
     *
     * @param args    条件
     * @param isQuery /
     * @return /
     * @throws Exception /
     */
    List<DeptDO> describeDeptList(QueryDeptArgs args, Boolean isQuery) throws Exception;

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    DeptDO describeDeptById(Long id);

    /**
     * 根据PID查询
     *
     * @param pid /
     * @return /
     */
    List<DeptDO> describeDeptListByPid(long pid);

    /**
     * 根据角色ID查询
     *
     * @param id /
     * @return /
     */
    Set<DeptDO> describeDeptByRoleId(Long id);

    /**
     * 获取部门下所有关联的部门
     *
     * @param deptDOList /
     * @param deptDOS    /
     * @return /
     */
    Set<DeptDO> describeRelationDeptSet(List<DeptDO> deptDOList, Set<DeptDO> deptDOS);

    /**
     * 根据ID获取同级与上级数据
     *
     * @param deptDO  /
     * @param deptDOS /
     * @return /
     */
    List<DeptDO> describeSuperiorDeptListByPid(DeptDO deptDO, List<DeptDO> deptDOS);

    /**
     * 构建树形数据
     *
     * @param deptDOS /
     * @return /
     */
    BaseResult<Object> buildDeptTree(List<DeptDO> deptDOS);

    /**
     * 获取
     *
     * @param deptDOList 、
     * @return 、
     */
    List<Long> describeChildDeptIdListByDeptIds(List<DeptDO> deptDOList);

    /**
     * 验证是否被角色或用户关联
     *
     * @param deptDOS /
     */
    void verifyBindRelationByIds(Set<DeptDO> deptDOS);

    /**
     * 遍历所有部门和子部门
     *
     * @param ids     /
     * @param deptDOS /
     */
    void traverseDeptByIdWithPids(Set<Long> ids, Set<DeptDO> deptDOS);

    /**
     * 创建
     *
     * @param args /
     */
    void saveDept(CreateDeptArgs args);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyDept(DeptDO resources);

    /**
     * 删除
     *
     * @param deptDOS /
     */
    void removeDeptByIds(Set<DeptDO> deptDOS);

    /**
     * 导出数据
     *
     * @param deptDOS  待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadDeptExcel(List<DeptDO> deptDOS, HttpServletResponse response) throws IOException;
}
