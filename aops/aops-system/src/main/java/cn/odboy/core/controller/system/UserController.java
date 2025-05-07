package cn.odboy.core.controller.system;

import cn.hutool.core.collection.CollectionUtil;
import cn.odboy.base.MyMetaOption;
import cn.odboy.base.PageResult;
import cn.odboy.core.api.system.SystemDataApi;
import cn.odboy.core.api.system.SystemDeptApi;
import cn.odboy.core.api.system.SystemRoleApi;
import cn.odboy.core.api.system.SystemUserApi;
import cn.odboy.core.constant.CaptchaBizEnum;
import cn.odboy.core.dal.dataobject.system.Dept;
import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.dal.model.UpdateUserPasswordResponse;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.framework.permission.util.SecurityHelper;
import cn.odboy.core.framework.properties.AppProperties;
import cn.odboy.core.service.system.SystemUserService;
import cn.odboy.core.service.system.dto.QueryUserRequest;
import cn.odboy.core.service.tools.CaptchaService;
import cn.odboy.exception.BadRequestException;
import cn.odboy.util.PageUtil;
import cn.odboy.util.RsaEncryptUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Api(tags = "系统：用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final SystemUserService systemUserService;
    private final SystemDataApi systemDataApi;
    private final SystemDeptApi systemDeptApi;
    private final SystemRoleApi systemRoleApi;
    private final SystemUserApi systemUserApi;
    private final CaptchaService verificationCodeService;
    private final AppProperties properties;

    @ApiOperation("导出用户数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('user:list')")
    public void downloadUserExcel(HttpServletResponse response, QueryUserRequest criteria) throws IOException {
        systemUserService.downloadUserExcel(systemUserApi.describeUserList(criteria), response);
    }

    @ApiOperation("查询用户")
    @GetMapping
    @PreAuthorize("@el.check('user:list')")
    public ResponseEntity<PageResult<User>> queryUser(QueryUserRequest criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        if (!ObjectUtils.isEmpty(criteria.getDeptId())) {
            criteria.getDeptIds().add(criteria.getDeptId());
            // 先查找是否存在子节点
            List<Dept> data = systemDeptApi.describeDeptListByPid(criteria.getDeptId());
            // 然后把子节点的ID都加入到集合中
            criteria.getDeptIds().addAll(systemDeptApi.describeChildDeptIdListByDeptIds(data));
        }
        // 数据权限
        List<Long> dataScopes = systemDataApi.describeDeptIdListByUserIdWithDeptId(systemUserApi.describeUserByUsername(SecurityHelper.getCurrentUsername()));
        // criteria.getDeptIds() 不为空并且数据权限不为空则取交集
        if (!CollectionUtils.isEmpty(criteria.getDeptIds()) && !CollectionUtils.isEmpty(dataScopes)) {
            // 取交集
            criteria.getDeptIds().retainAll(dataScopes);
            if (!CollectionUtil.isEmpty(criteria.getDeptIds())) {
                return new ResponseEntity<>(systemUserApi.describeUserPage(criteria, page), HttpStatus.OK);
            }
        } else {
            // 否则取并集
            criteria.getDeptIds().addAll(dataScopes);
            return new ResponseEntity<>(systemUserApi.describeUserPage(criteria, page), HttpStatus.OK);
        }
        return new ResponseEntity<>(PageUtil.noData(), HttpStatus.OK);
    }

    @ApiOperation("新增用户")
    @PostMapping(value = "/saveUser")
    @PreAuthorize("@el.check('user:add')")
    public ResponseEntity<Object> saveUser(@Validated @RequestBody User resources) {
        checkLevel(resources);
        // 默认密码 123456
        resources.setPassword(passwordEncoder.encode("123456"));
        systemUserService.saveUser(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("修改用户")
    @PostMapping(value = "/modifyUserById")
    @PreAuthorize("@el.check('user:edit')")
    public ResponseEntity<Object> modifyUserById(@Validated(User.Update.class) @RequestBody User resources) throws Exception {
        checkLevel(resources);
        systemUserService.modifyUserById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("修改用户：个人中心")
    @PostMapping(value = "modifyUserCenterInfoById")
    public ResponseEntity<Object> modifyUserCenterInfoById(@Validated(User.Update.class) @RequestBody User resources) {
        if (!resources.getId().equals(SecurityHelper.getCurrentUserId())) {
            throw new BadRequestException("不能修改他人资料");
        }
        systemUserService.modifyUserCenterInfoById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("删除用户")
    @PostMapping(value = "/removeUserByIds")
    @PreAuthorize("@el.check('user:del')")
    public ResponseEntity<Object> removeUserByIds(@RequestBody Set<Long> ids) {
        for (Long id : ids) {
            Integer currentLevel = Collections.min(systemRoleApi.describeRoleListByUsersId(SecurityHelper.getCurrentUserId()).stream().map(Role::getLevel).collect(Collectors.toList()));
            Integer optLevel = Collections.min(systemRoleApi.describeRoleListByUsersId(id).stream().map(Role::getLevel).collect(Collectors.toList()));
            if (currentLevel > optLevel) {
                throw new BadRequestException("角色权限不足，不能删除：" + systemUserApi.describeUserById(id).getUsername());
            }
        }
        systemUserService.removeUserByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("修改密码")
    @PostMapping(value = "/modifyUserPasswordByUsername")
    public ResponseEntity<Object> modifyUserPasswordByUsername(@RequestBody UpdateUserPasswordResponse passVo) throws Exception {
        String oldPass = RsaEncryptUtil.decryptByPrivateKey(properties.getRsa().getPrivateKey(), passVo.getOldPass());
        String newPass = RsaEncryptUtil.decryptByPrivateKey(properties.getRsa().getPrivateKey(), passVo.getNewPass());
        User user = systemUserApi.describeUserByUsername(SecurityHelper.getCurrentUsername());
        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            throw new BadRequestException("修改失败，旧密码错误");
        }
        if (passwordEncoder.matches(newPass, user.getPassword())) {
            throw new BadRequestException("新密码不能与旧密码相同");
        }
        systemUserService.modifyUserPasswordByUsername(user.getUsername(), passwordEncoder.encode(newPass));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("重置密码")
    @PostMapping(value = "/resetUserPasswordByIds")
    public ResponseEntity<Object> resetUserPasswordByIds(@RequestBody Set<Long> ids) {
        String defaultPwd = passwordEncoder.encode("123456");
        systemUserService.resetUserPasswordByIds(ids, defaultPwd);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("修改头像")
    @PostMapping(value = "/modifyUserAvatar")
    public ResponseEntity<Object> modifyUserAvatar(@RequestParam MultipartFile avatar) {
        return new ResponseEntity<>(systemUserService.modifyUserAvatar(avatar), HttpStatus.OK);
    }

    @ApiOperation("修改邮箱")
    @PostMapping(value = "/modifyUserEmailByUsername/{code}")
    public ResponseEntity<Object> modifyUserEmailByUsername(@PathVariable String code, @RequestBody User resources) throws Exception {
        String password = RsaEncryptUtil.decryptByPrivateKey(properties.getRsa().getPrivateKey(), resources.getPassword());
        User user = systemUserApi.describeUserByUsername(SecurityHelper.getCurrentUsername());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("密码错误");
        }
        verificationCodeService.checkCodeAvailable(CaptchaBizEnum.EMAIL_RESET_EMAIL_CODE.getBizCode(), resources.getEmail(), code);
        systemUserService.modifyUserEmailByUsername(user.getUsername(), resources.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
     *
     * @param resources /
     */
    private void checkLevel(User resources) {
        Integer currentLevel = Collections.min(systemRoleApi.describeRoleListByUsersId(SecurityHelper.getCurrentUserId()).stream().map(Role::getLevel).collect(Collectors.toList()));
        Integer optLevel = systemRoleApi.describeDeptLevelByRoles(resources.getRoles());
        if (currentLevel > optLevel) {
            throw new BadRequestException("角色权限不足");
        }
    }

    @ApiOperation("查询用户基础数据")
    @PostMapping(value = "/describeUserMetadataOptions")
    @PreAuthorize("@el.check('user:list')")
    public ResponseEntity<List<MyMetaOption>> queryUserMetadataOptions(@Validated @RequestBody QueryUserRequest criteria) {
        int maxPageSize = 50;
        return new ResponseEntity<>(systemUserService.page(new Page<>(criteria.getPage(), maxPageSize), new LambdaQueryWrapper<User>()
                .and(c -> {
                    c.eq(User::getPhone, criteria.getBlurry());
                    c.or();
                    c.eq(User::getEmail, criteria.getBlurry());
                    c.or();
                    c.like(User::getUsername, criteria.getBlurry());
                    c.or();
                    c.like(User::getNickName, criteria.getBlurry());
                })
        ).getRecords().stream().map(m -> {
            Map<String, Object> ext = new HashMap<>(1);
            ext.put("id", m.getId());
            ext.put("deptId", m.getDeptId());
            ext.put("email", m.getEmail());
            ext.put("phone", m.getPhone());
            return MyMetaOption.builder()
                    .label(m.getNickName())
                    .value(m.getUsername())
                    .ext(ext)
                    .build();
        }).collect(Collectors.toList()), HttpStatus.OK);
    }
}
