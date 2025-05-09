package cn.odboy.core.service.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.dal.dataobject.system.RoleDO;
import cn.odboy.core.dal.dataobject.system.UserDO;
import cn.odboy.core.service.system.dto.CreateRoleArgs;
import cn.odboy.core.service.system.dto.QueryRoleArgs;
import cn.odboy.core.controller.system.vo.RoleCodeVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemRoleService extends IService<RoleDO> {

    /**
     * 查询全部数据
     *
     * @return /
     */
    List<RoleDO> describeRoleList();

    /**
     * 查询全部
     *
     * @param args 条件
     * @return /
     */
    List<RoleDO> describeRoleList(QueryRoleArgs args);

    /**
     * 待条件分页查询
     *
     * @param args 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<RoleDO> describeRolePage(QueryRoleArgs args, Page<Object> page);

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    RoleDO describeRoleById(long id);


    /**
     * 根据用户ID查询
     *
     * @param userId 用户ID
     * @return /
     */
    List<RoleDO> describeRoleListByUsersId(Long userId);

    /**
     * 根据角色查询角色级别
     *
     * @param roleDOS /
     * @return /
     */
    Integer describeDeptLevelByRoles(Set<RoleDO> roleDOS);

    /**
     * 获取用户权限信息
     *
     * @param userDO 用户信息
     * @return 权限信息
     */
    List<RoleCodeVo> buildUserRolePermissions(UserDO userDO);
    /**
     * 验证是否被用户关联
     *
     * @param ids /
     */
    void verifyBindRelationByIds(Set<Long> ids);

    /**
     * 根据菜单Id查询
     *
     * @param menuId /
     * @return /
     */
    List<RoleDO> describeRoleListByMenuId(Long menuId);

    /**
     * 创建
     *
     * @param args /
     */
    void saveRole(CreateRoleArgs args);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyRoleById(RoleDO resources);

    /**
     * 修改绑定的菜单
     *
     * @param resources /
     */
    void modifyBindMenuById(RoleDO resources);

    /**
     * 删除
     *
     * @param ids /
     */
    void removeRoleByIds(Set<Long> ids);


    /**
     * 导出数据
     *
     * @param roleDOS    待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadRoleExcel(List<RoleDO> roleDOS, HttpServletResponse response) throws IOException;

}
