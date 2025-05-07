package cn.odboy.core.service.system.impl;

import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.Job;
import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.dal.mysql.system.UserJobMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.dal.mysql.system.UserRoleMapper;
import cn.odboy.core.framework.permission.util.SecurityHelper;
import cn.odboy.core.framework.properties.AppProperties;
import cn.odboy.core.cache.service.SystemUserJwtService;
import cn.odboy.core.cache.service.SystemUserOnlineService;
import cn.odboy.core.service.system.SystemUserService;
import cn.odboy.exception.BadRequestException;
import cn.odboy.exception.EntityExistException;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.FileUtil;
import cn.odboy.util.StringUtil;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<UserMapper, User> implements SystemUserService {
    private final UserMapper userMapper;
    private final UserJobMapper userJobMapper;
    private final UserRoleMapper userRoleMapper;
    private final AppProperties properties;
    private final RedisHelper redisHelper;
    private final SystemUserJwtService systemUserJwtService;
    private final SystemUserOnlineService systemUserOnlineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User resources) {
        resources.setDeptId(resources.getDept().getId());
        if (userMapper.getUserByUsername(resources.getUsername()) != null) {
            throw new EntityExistException(User.class, "username", resources.getUsername());
        }
        if (userMapper.getUserByEmail(resources.getEmail()) != null) {
            throw new EntityExistException(User.class, "email", resources.getEmail());
        }
        if (userMapper.getUserByPhone(resources.getPhone()) != null) {
            throw new EntityExistException(User.class, "phone", resources.getPhone());
        }
        save(resources);
        // 保存用户岗位
        userJobMapper.insertBatchWithUserId(resources.getJobs(), resources.getId());
        // 保存用户角色
        userRoleMapper.insertBatchWithUserId(resources.getRoles(), resources.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserById(User resources) {
        User user = getById(resources.getId());
        User user1 = userMapper.getUserByUsername(resources.getUsername());
        User user2 = userMapper.getUserByEmail(resources.getEmail());
        User user3 = userMapper.getUserByPhone(resources.getPhone());
        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(User.class, "username", resources.getUsername());
        }
        if (user2 != null && !user.getId().equals(user2.getId())) {
            throw new EntityExistException(User.class, "email", resources.getEmail());
        }
        if (user3 != null && !user.getId().equals(user3.getId())) {
            throw new EntityExistException(User.class, "phone", resources.getPhone());
        }
        // 如果用户的角色改变
        if (!resources.getRoles().equals(user.getRoles())) {
            redisHelper.del(SystemRedisKey.DATA_USER + resources.getId());
            redisHelper.del(SystemRedisKey.MENU_USER + resources.getId());
            redisHelper.del(SystemRedisKey.ROLE_AUTH + resources.getId());
            redisHelper.del(SystemRedisKey.ROLE_USER + resources.getId());
        }
        // 修改部门会影响 数据权限
        if (!Objects.equals(resources.getDept(), user.getDept())) {
            redisHelper.del(SystemRedisKey.DATA_USER + resources.getId());
        }
        // 如果用户被禁用，则清除用户登录信息
        if (!resources.getEnabled()) {
            systemUserOnlineService.kickOutByUsername(resources.getUsername());
        }
        user.setDeptId(resources.getDept().getId());
        user.setUsername(resources.getUsername());
        user.setEmail(resources.getEmail());
        user.setEnabled(resources.getEnabled());
        user.setRoles(resources.getRoles());
        user.setDept(resources.getDept());
        user.setJobs(resources.getJobs());
        user.setPhone(resources.getPhone());
        user.setNickName(resources.getNickName());
        user.setGender(resources.getGender());
        saveOrUpdate(user);
        // 清除缓存
        delCaches(user.getId(), user.getUsername());
        // 更新用户岗位
        userJobMapper.deleteByUserId(resources.getId());
        userJobMapper.insertBatchWithUserId(resources.getJobs(), resources.getId());
        // 更新用户角色
        userRoleMapper.deleteByUserId(resources.getId());
        userRoleMapper.insertBatchWithUserId(resources.getRoles(), resources.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserCenterInfoById(User resources) {
        User user = getById(resources.getId());
        User user1 = userMapper.getUserByPhone(resources.getPhone());
        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(User.class, "phone", resources.getPhone());
        }
        user.setNickName(resources.getNickName());
        user.setPhone(resources.getPhone());
        user.setGender(resources.getGender());
        saveOrUpdate(user);
        // 清理缓存
        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserByIds(Set<Long> ids) {
        for (Long id : ids) {
            // 清理缓存
            User user = getById(id);
            delCaches(user.getId(), user.getUsername());
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
        List<User> users = userMapper.selectByIds(ids);
        // 清除缓存
        users.forEach(user -> {
            // 清除缓存
            flushCache(user.getUsername());
            // 强制退出
            systemUserOnlineService.kickOutByUsername(user.getUsername());
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
        User user = userMapper.getUserByUsername(SecurityHelper.getCurrentUsername());
        String oldPath = user.getAvatarPath();
        File file = FileUtil.upload(multipartFile, properties.getFile().getPath().getAvatar());
        user.setAvatarPath(Objects.requireNonNull(file).getPath());
        user.setAvatarName(file.getName());
        saveOrUpdate(user);
        if (StringUtil.isNotBlank(oldPath)) {
            FileUtil.del(oldPath);
        }
        @NotBlank String username = user.getUsername();
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
    public void downloadUserExcel(List<User> users, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (User user : users) {
            List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", user.getUsername());
            map.put("角色", roles);
            map.put("部门", user.getDept().getName());
            map.put("岗位", user.getJobs().stream().map(Job::getName).collect(Collectors.toList()));
            map.put("邮箱", user.getEmail());
            map.put("状态", user.getEnabled() ? "启用" : "禁用");
            map.put("手机号码", user.getPhone());
            map.put("修改密码的时间", user.getPwdResetTime());
            map.put("创建日期", user.getCreateTime());
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
        redisHelper.del(SystemRedisKey.USER_ID + id);
        flushCache(username);
    }

    /**
     * 清理 登陆时 用户缓存信息
     *
     * @param username /
     */
    private void flushCache(String username) {
        systemUserJwtService.cleanUserJwtModelCacheByUsername(username);
    }
}
