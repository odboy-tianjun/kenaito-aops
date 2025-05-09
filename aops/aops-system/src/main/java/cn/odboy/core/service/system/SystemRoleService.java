package cn.odboy.core.service.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.service.system.dto.CreateRoleRequest;
import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.service.system.dto.QueryRoleRequest;
import cn.odboy.core.service.system.dto.RoleCodeVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface SystemRoleService extends IService<Role> {

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

    /**
     * 创建
     *
     * @param resources /
     */
    void saveRole(CreateRoleRequest resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void modifyRoleById(Role resources);

    /**
     * 修改绑定的菜单
     *
     * @param resources /
     */
    void modifyBindMenuById(Role resources);

    /**
     * 删除
     *
     * @param ids /
     */
    void removeRoleByIds(Set<Long> ids);


    /**
     * 导出数据
     *
     * @param roles    待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadRoleExcel(List<Role> roles, HttpServletResponse response) throws IOException;

}
