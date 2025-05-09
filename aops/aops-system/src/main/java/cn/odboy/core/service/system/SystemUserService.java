package cn.odboy.core.service.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.dal.dataobject.system.UserDO;
import cn.odboy.core.service.system.dto.QueryUserArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SystemUserService extends IService<UserDO> {

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return /
     */
    UserDO describeUserById(long id);

    /**
     * 根据用户名查询
     *
     * @param username /
     * @return /
     */
    UserDO describeUserByUsername(String username);

    /**
     * 查询全部
     *
     * @param args 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<UserDO> describeUserPage(QueryUserArgs args, Page<Object> page);

    /**
     * 查询全部不分页
     *
     * @param args 条件
     * @return /
     */
    List<UserDO> describeUserList(QueryUserArgs args);

    /**
     * 新增用户
     *
     * @param resources /
     */
    void saveUser(UserDO resources);

    /**
     * 编辑用户
     *
     * @param resources /
     * @throws Exception /
     */
    void modifyUserById(UserDO resources) throws Exception;

    /**
     * 删除用户
     *
     * @param ids /
     */
    void removeUserByIds(Set<Long> ids);
    /**
     * 修改密码
     *
     * @param username        用户名
     * @param encryptPassword 密码
     */
    void modifyUserPasswordByUsername(String username, String encryptPassword);

    /**
     * 修改头像
     *
     * @param file 文件
     * @return /
     */
    Map<String, String> modifyUserAvatar(MultipartFile file);

    /**
     * 修改邮箱
     *
     * @param username 用户名
     * @param email    邮箱
     */
    void modifyUserEmailByUsername(String username, String email);

    /**
     * 导出数据
     *
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void downloadUserExcel(List<UserDO> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 用户自助修改资料
     *
     * @param resources /
     */
    void modifyUserCenterInfoById(UserDO resources);

    /**
     * 重置密码
     *
     * @param ids      用户id
     * @param password 密码
     */
    void resetUserPasswordByIds(Set<Long> ids, String password);
}
