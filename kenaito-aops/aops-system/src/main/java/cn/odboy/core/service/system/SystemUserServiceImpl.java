package cn.odboy.core.service.system;

import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.exception.EntityExistException;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.PageUtil;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.dal.dataobject.system.JobDO;
import cn.odboy.core.dal.dataobject.system.RoleDO;
import cn.odboy.core.dal.dataobject.system.UserDO;
import cn.odboy.core.dal.mysql.system.UserJobMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.dal.mysql.system.UserRoleMapper;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.dal.redis.system.SystemUserJwtInfoDAO;
import cn.odboy.core.dal.redis.system.SystemUserOnlineInfoDAO;
import cn.odboy.core.framework.permission.core.util.SecurityHelper;
import cn.odboy.core.framework.system.config.AppProperties;
import cn.odboy.core.service.system.dto.UserQueryArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements SystemUserService {
    private final UserMapper userMapper;
    private final UserJobMapper userJobMapper;
    private final UserRoleMapper userRoleMapper;
    private final AppProperties properties;
    private final RedisHelper redisHelper;
    private final SystemUserJwtInfoDAO systemUserJwtInfoDAO;
    private final SystemUserOnlineInfoDAO systemUserOnlineInfoDAO;

    @Override
    public PageResult<UserDO> describeUserPage(UserQueryArgs args, Page<Object> page) {
        args.setOffset(page.offset());
        List<UserDO> userDOS = userMapper.queryUserPageByArgs(args, PageUtil.getCount(userMapper)).getRecords();
        Long total = userMapper.getUserCountByArgs(args);
        return PageUtil.toPage(userDOS, total);
    }

    @Override
    public List<UserDO> describeUserList(UserQueryArgs args) {
        return userMapper.queryUserPageByArgs(args, PageUtil.getCount(userMapper)).getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDO describeUserById(long id) {
        String key = RedisKeyConst.USER_ID + id;
        UserDO userDO = redisHelper.get(key, UserDO.class);
        if (userDO == null) {
            userDO = userMapper.selectById(id);
            redisHelper.set(key, userDO, 1, TimeUnit.DAYS);
        }
        return userDO;
    }

    @Override
    public UserDO describeUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(UserDO resources) {
        resources.setDeptId(resources.getDept().getId());
        if (userMapper.getUserByUsername(resources.getUsername()) != null) {
            throw new EntityExistException(UserDO.class, "username", resources.getUsername());
        }
        if (userMapper.getUserByEmail(resources.getEmail()) != null) {
            throw new EntityExistException(UserDO.class, "email", resources.getEmail());
        }
        if (userMapper.getUserByPhone(resources.getPhone()) != null) {
            throw new EntityExistException(UserDO.class, "phone", resources.getPhone());
        }
        save(resources);
        // 保存用户岗位
        userJobMapper.insertBatchWithUserId(resources.getJobs(), resources.getId());
        // 保存用户角色
        userRoleMapper.insertBatchWithUserId(resources.getRoles(), resources.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserById(UserDO resources) {
        UserDO userDO = getById(resources.getId());
        UserDO userDO1 = userMapper.getUserByUsername(resources.getUsername());
        UserDO userDO2 = userMapper.getUserByEmail(resources.getEmail());
        UserDO userDO3 = userMapper.getUserByPhone(resources.getPhone());
        if (userDO1 != null && !userDO.getId().equals(userDO1.getId())) {
            throw new EntityExistException(UserDO.class, "username", resources.getUsername());
        }
        if (userDO2 != null && !userDO.getId().equals(userDO2.getId())) {
            throw new EntityExistException(UserDO.class, "email", resources.getEmail());
        }
        if (userDO3 != null && !userDO.getId().equals(userDO3.getId())) {
            throw new EntityExistException(UserDO.class, "phone", resources.getPhone());
        }
        // 如果用户的角色改变
        if (!resources.getRoles().equals(userDO.getRoles())) {
            redisHelper.del(RedisKeyConst.DATA_USER + resources.getId());
            redisHelper.del(RedisKeyConst.MENU_USER + resources.getId());
            redisHelper.del(RedisKeyConst.ROLE_AUTH + resources.getId());
            redisHelper.del(RedisKeyConst.ROLE_USER + resources.getId());
        }
        // 修改部门会影响 数据权限
        if (!Objects.equals(resources.getDept(), userDO.getDept())) {
            redisHelper.del(RedisKeyConst.DATA_USER + resources.getId());
        }
        // 如果用户被禁用，则清除用户登录信息
        if (!resources.getEnabled()) {
            systemUserOnlineInfoDAO.kickOutByUsername(resources.getUsername());
        }
        userDO.setDeptId(resources.getDept().getId());
        userDO.setUsername(resources.getUsername());
        userDO.setEmail(resources.getEmail());
        userDO.setEnabled(resources.getEnabled());
        userDO.setRoles(resources.getRoles());
        userDO.setDept(resources.getDept());
        userDO.setJobs(resources.getJobs());
        userDO.setPhone(resources.getPhone());
        userDO.setNickName(resources.getNickName());
        userDO.setGender(resources.getGender());
        saveOrUpdate(userDO);
        // 清除缓存
        delCaches(userDO.getId(), userDO.getUsername());
        // 更新用户岗位
        userJobMapper.deleteByUserId(resources.getId());
        userJobMapper.insertBatchWithUserId(resources.getJobs(), resources.getId());
        // 更新用户角色
        userRoleMapper.deleteByUserId(resources.getId());
        userRoleMapper.insertBatchWithUserId(resources.getRoles(), resources.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserCenterInfoById(UserDO resources) {
        UserDO userDO = getById(resources.getId());
        UserDO userDO1 = userMapper.getUserByPhone(resources.getPhone());
        if (userDO1 != null && !userDO.getId().equals(userDO1.getId())) {
            throw new EntityExistException(UserDO.class, "phone", resources.getPhone());
        }
        userDO.setNickName(resources.getNickName());
        userDO.setPhone(resources.getPhone());
        userDO.setGender(resources.getGender());
        saveOrUpdate(userDO);
        // 清理缓存
        delCaches(userDO.getId(), userDO.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserByIds(Set<Long> ids) {
        for (Long id : ids) {
            // 清理缓存
            UserDO userDO = getById(id);
            delCaches(userDO.getId(), userDO.getUsername());
        }
        userMapper.deleteByIds(ids);
        // 删除用户岗位
        userJobMapper.deleteByUserIds(ids);
        // 删除用户角色
        userRoleMapper.deleteByUserIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserPasswordByUsername(String username, String pass) {
        userMapper.updatePasswordByUsername(username, pass, new Date());
        flushCache(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetUserPasswordByIds(Set<Long> ids, String password) {
        List<UserDO> userDOS = userMapper.selectByIds(ids);
        // 清除缓存
        userDOS.forEach(user -> {
            // 清除缓存
            flushCache(user.getUsername());
            // 强制退出
            systemUserOnlineInfoDAO.kickOutByUsername(user.getUsername());
        });
        // 重置密码
        userMapper.updatePasswordByUserIds(password, ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> modifyUserAvatar(MultipartFile multipartFile) {
        // 文件大小验证
        FileUtil.checkSize(properties.getFile().getAvatarMaxSize(), multipartFile.getSize());
        // 验证文件上传的格式
        String image = "gif jpg png jpeg";
        String fileType = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (fileType != null && !image.contains(fileType)) {
            throw new BadRequestException("文件格式错误！, 仅支持 " + image + " 格式");
        }
        UserDO userDO = userMapper.getUserByUsername(SecurityHelper.getCurrentUsername());
        String oldPath = userDO.getAvatarPath();
        File file = FileUtil.upload(multipartFile, properties.getFile().getPath().getAvatar());
        userDO.setAvatarPath(Objects.requireNonNull(file).getPath());
        userDO.setAvatarName(file.getName());
        saveOrUpdate(userDO);
        if (StringUtil.isNotBlank(oldPath)) {
            FileUtil.del(oldPath);
        }
        @NotBlank String username = userDO.getUsername();
        flushCache(username);
        return new HashMap<>(1) {{
            put("avatar", file.getName());
        }};
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserEmailByUsername(String username, String email) {
        userMapper.updateEmailByUsername(username, email);
        flushCache(username);
    }

    @Override
    public void downloadUserExcel(List<UserDO> userDOS, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserDO userDO : userDOS) {
            List<String> roles = userDO.getRoles().stream().map(RoleDO::getName).collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", userDO.getUsername());
            map.put("角色", roles);
            map.put("部门", userDO.getDept().getName());
            map.put("岗位", userDO.getJobs().stream().map(JobDO::getName).collect(Collectors.toList()));
            map.put("邮箱", userDO.getEmail());
            map.put("状态", userDO.getEnabled() ? "启用" : "禁用");
            map.put("手机号码", userDO.getPhone());
            map.put("修改密码的时间", userDO.getPwdResetTime());
            map.put("创建日期", userDO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 清理缓存
     *
     * @param id /
     */
    public void delCaches(Long id, String username) {
        redisHelper.del(RedisKeyConst.USER_ID + id);
        flushCache(username);
    }

    /**
     * 清理 登陆时 用户缓存信息
     *
     * @param username /
     */
    private void flushCache(String username) {
        systemUserJwtInfoDAO.cleanUserJwtModelCacheByUsername(username);
    }
}
