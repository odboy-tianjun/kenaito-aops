package cn.odboy.core.api.system;

import cn.odboy.base.PageResult;
import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.service.system.dto.QueryRoleRequest;
import cn.odboy.core.service.system.dto.RoleCodeVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Set;

public interface SystemRoleApi {

    /**
     * 查询全部数据
     *
     * @return /
     */
    List<Role> describeRoleList();

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return /
     */
    List<Role> describeRoleList(QueryRoleRequest criteria);

    /**
     * 待条件分页查询
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<Role> describeRolePage(QueryRoleRequest criteria, Page<Object> page);

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    Role describeRoleById(long id);


    /**
     * 根据用户ID查询
     *
     * @param userId 用户ID
     * @return /
     */
    List<Role> describeRoleListByUsersId(Long userId);

    /**
     * 根据角色查询角色级别
     *
     * @param roles /
     * @return /
     */
    Integer describeDeptLevelByRoles(Set<Role> roles);

    /**
     * 获取用户权限信息
     *
     * @param user 用户信息
     * @return 权限信息
     */
    List<RoleCodeVo> buildUserRolePermissions(User user);
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
    List<Role> describeRoleListByMenuId(Long menuId);
}
