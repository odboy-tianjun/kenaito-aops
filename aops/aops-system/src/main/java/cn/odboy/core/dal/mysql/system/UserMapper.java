package cn.odboy.core.dal.mysql.system;

import cn.odboy.core.service.system.dto.QueryUserRequest;
import cn.odboy.core.dal.dataobject.system.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    Long getUserCountByArgs(@Param("criteria") QueryUserRequest criteria);

    IPage<User> queryUserPageByArgs(@Param("criteria") QueryUserRequest criteria, Page<Object> page);

    User getUserByUsername(@Param("username") String username);

    User getUserByEmail(@Param("email") String email);

    User getUserByPhone(@Param("phone") String phone);

    void updatePasswordByUsername(@Param("username") String username, @Param("password") String password, @Param("lastPasswordResetTime") Date lastPasswordResetTime);

    void updateEmailByUsername(@Param("username") String username, @Param("email") String email);

    List<User> queryUserListByRoleId(@Param("roleId") Long roleId);

    List<User> queryUserListByDeptId(@Param("deptId") Long deptId);

    List<User> queryUserListByMenuId(@Param("menuId") Long menuId);

    int getUserCountByJobIds(@Param("jobIds") Set<Long> jobIds);

    int getUserCountByDeptIds(@Param("deptIds") Set<Long> deptIds);

    int getUserCountByRoleIds(@Param("roleIds") Set<Long> roleIds);

    void updatePasswordByUserIds(@Param("pwd") String password, @Param("userIds") Set<Long> userIds);
}
