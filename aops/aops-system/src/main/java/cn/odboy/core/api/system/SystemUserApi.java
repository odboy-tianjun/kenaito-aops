package cn.odboy.core.api.system;

import cn.odboy.base.PageResult;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.service.system.dto.QueryUserRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface SystemUserApi {

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return /
     */
    User describeUserById(long id);

    /**
     * 根据用户名查询
     *
     * @param username /
     * @return /
     */
    User describeUserByUsername(String username);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<User> describeUserPage(QueryUserRequest criteria, Page<Object> page);

    /**
     * 查询全部不分页
     *
     * @param criteria 条件
     * @return /
     */
    List<User> describeUserList(QueryUserRequest criteria);

}
