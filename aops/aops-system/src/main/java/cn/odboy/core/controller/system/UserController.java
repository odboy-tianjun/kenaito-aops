package cn.odboy.core.controller.system;

import cn.hutool.core.collection.CollectionUtil;
import cn.odboy.common.pojo.MyMetaOptionItem;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.dal.dataobject.system.DeptDO;
import cn.odboy.core.dal.dataobject.system.RoleDO;
import cn.odboy.core.dal.dataobject.system.UserDO;
import cn.odboy.core.framework.operalog.annotaions.OperationLog;
import cn.odboy.core.service.system.SystemDataService;
import cn.odboy.core.constant.CaptchaBizEnum;
import cn.odboy.core.framework.permission.core.util.SecurityHelper;
import cn.odboy.core.framework.system.config.AppProperties;
import cn.odboy.core.service.system.SystemDeptService;
import cn.odboy.core.service.system.SystemRoleService;
import cn.odboy.core.service.system.SystemUserService;
import cn.odboy.core.service.system.dto.UserQueryArgs;
import cn.odboy.core.service.tools.CaptchaService;
import cn.odboy.core.controller.system.vo.UserModifyPasswordVo;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.util.PageUtil;
import cn.odboy.common.util.RsaEncryptUtil;
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
    private final SystemDataService systemDataService;
    private final SystemDeptService systemDeptService;
    private final SystemRoleService systemRoleService;
    private final CaptchaService verificationCodeService;
    private final AppProperties properties;

    @OperationLog
    @ApiOperation("导出用户数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('user:list')")
    public void exportUser(HttpServletResponse response, UserQueryArgs args) throws IOException {
        systemUserService.downloadUserExcel(systemUserService.describeUserList(args), response);
    }

    @ApiOperation("查询用户")
    @GetMapping
    @PreAuthorize("@el.check('user:list')")
    public ResponseEntity<PageResult<UserDO>> queryUser(UserQueryArgs args) {
        Page<Object> page = new Page<>(args.getPage(), args.getSize());
        if (!ObjectUtils.isEmpty(args.getDeptId())) {
            args.getDeptIds().add(args.getDeptId());
            // 先查找是否存在子节点
            List<DeptDO> data = systemDeptService.describeDeptListByPid(args.getDeptId());
            // 然后把子节点的ID都加入到集合中
            args.getDeptIds().addAll(systemDeptService.describeChildDeptIdListByDeptIds(data));
        }
        // 数据权限
        List<Long> dataScopes = systemDataService.describeDeptIdListByUserIdWithDeptId(systemUserService.describeUserByUsername(SecurityHelper.getCurrentUsername()));
        // criteria.getDeptIds() 不为空并且数据权限不为空则取交集
        if (!CollectionUtils.isEmpty(args.getDeptIds()) && !CollectionUtils.isEmpty(dataScopes)) {
            // 取交集
            args.getDeptIds().retainAll(dataScopes);
            if (!CollectionUtil.isEmpty(args.getDeptIds())) {
                return new ResponseEntity<>(systemUserService.describeUserPage(args, page), HttpStatus.OK);
            }
        } else {
            // 否则取并集
            args.getDeptIds().addAll(dataScopes);
            return new ResponseEntity<>(systemUserService.describeUserPage(args, page), HttpStatus.OK);
        }
        return new ResponseEntity<>(PageUtil.noData(), HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("新增用户")
    @PostMapping(value = "/saveUser")
    @PreAuthorize("@el.check('user:add')")
    public ResponseEntity<Object> saveUser(@Validated @RequestBody UserDO resources) {
        checkLevel(resources);
        // 默认密码 123456
        resources.setPassword(passwordEncoder.encode("123456"));
        systemUserService.saveUser(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @OperationLog
    @ApiOperation("修改用户")
    @PostMapping(value = "/modifyUserById")
    @PreAuthorize("@el.check('user:edit')")
    public ResponseEntity<Object> modifyUserById(@Validated(UserDO.Update.class) @RequestBody UserDO resources) throws Exception {
        checkLevel(resources);
        systemUserService.modifyUserById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @OperationLog
    @ApiOperation("修改用户：个人中心")
    @PostMapping(value = "modifyUserCenterInfoById")
    public ResponseEntity<Object> modifyUserCenterInfoById(@Validated(UserDO.Update.class) @RequestBody UserDO resources) {
        if (!resources.getId().equals(SecurityHelper.getCurrentUserId())) {
            throw new BadRequestException("不能修改他人资料");
        }
        systemUserService.modifyUserCenterInfoById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @OperationLog
    @ApiOperation("删除用户")
    @PostMapping(value = "/removeUserByIds")
    @PreAuthorize("@el.check('user:del')")
    public ResponseEntity<Object> removeUserByIds(@RequestBody Set<Long> ids) {
        for (Long id : ids) {
            Integer currentLevel = Collections.min(systemRoleService.describeRoleListByUsersId(SecurityHelper.getCurrentUserId()).stream().map(RoleDO::getLevel).collect(Collectors.toList()));
            Integer optLevel = Collections.min(systemRoleService.describeRoleListByUsersId(id).stream().map(RoleDO::getLevel).collect(Collectors.toList()));
            if (currentLevel > optLevel) {
                throw new BadRequestException("角色权限不足，不能删除：" + systemUserService.describeUserById(id).getUsername());
            }
        }
        systemUserService.removeUserByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @OperationLog
    @ApiOperation("修改密码")
    @PostMapping(value = "/modifyUserPasswordByUsername")
    public ResponseEntity<Object> modifyUserPasswordByUsername(@RequestBody UserModifyPasswordVo passVo) throws Exception {
        String oldPass = RsaEncryptUtil.decryptByPrivateKey(properties.getRsa().getPrivateKey(), passVo.getOldPass());
        String newPass = RsaEncryptUtil.decryptByPrivateKey(properties.getRsa().getPrivateKey(), passVo.getNewPass());
        UserDO userDO = systemUserService.describeUserByUsername(SecurityHelper.getCurrentUsername());
        if (!passwordEncoder.matches(oldPass, userDO.getPassword())) {
            throw new BadRequestException("修改失败，旧密码错误");
        }
        if (passwordEncoder.matches(newPass, userDO.getPassword())) {
            throw new BadRequestException("新密码不能与旧密码相同");
        }
        systemUserService.modifyUserPasswordByUsername(userDO.getUsername(), passwordEncoder.encode(newPass));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @OperationLog
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

    @OperationLog
    @ApiOperation("修改邮箱")
    @PostMapping(value = "/modifyUserEmailByUsername/{code}")
    public ResponseEntity<Object> modifyUserEmailByUsername(@PathVariable String code, @RequestBody UserDO resources) throws Exception {
        String password = RsaEncryptUtil.decryptByPrivateKey(properties.getRsa().getPrivateKey(), resources.getPassword());
        UserDO userDO = systemUserService.describeUserByUsername(SecurityHelper.getCurrentUsername());
        if (!passwordEncoder.matches(password, userDO.getPassword())) {
            throw new BadRequestException("密码错误");
        }
        verificationCodeService.checkCodeAvailable(CaptchaBizEnum.EMAIL_RESET_EMAIL_CODE.getBizCode(), resources.getEmail(), code);
        systemUserService.modifyUserEmailByUsername(userDO.getUsername(), resources.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
     *
     * @param resources /
     */
    private void checkLevel(UserDO resources) {
        Integer currentLevel = Collections.min(systemRoleService.describeRoleListByUsersId(SecurityHelper.getCurrentUserId()).stream().map(RoleDO::getLevel).collect(Collectors.toList()));
        Integer optLevel = systemRoleService.describeDeptLevelByRoles(resources.getRoles());
        if (currentLevel > optLevel) {
            throw new BadRequestException("角色权限不足");
        }
    }

    @ApiOperation("查询用户基础数据")
    @PostMapping(value = "/describeUserMetadataOptions")
    @PreAuthorize("@el.check('user:list')")
    public ResponseEntity<List<MyMetaOptionItem>> queryUserMetadataOptions(@Validated @RequestBody UserQueryArgs args) {
        int maxPageSize = 50;
        return new ResponseEntity<>(systemUserService.page(new Page<>(args.getPage(), maxPageSize), new LambdaQueryWrapper<UserDO>()
                .and(c -> {
                    c.eq(UserDO::getPhone, args.getBlurry());
                    c.or();
                    c.eq(UserDO::getEmail, args.getBlurry());
                    c.or();
                    c.like(UserDO::getUsername, args.getBlurry());
                    c.or();
                    c.like(UserDO::getNickName, args.getBlurry());
                })
        ).getRecords().stream().map(m -> {
            Map<String, Object> ext = new HashMap<>(1);
            ext.put("id", m.getId());
            ext.put("deptId", m.getDeptId());
            ext.put("email", m.getEmail());
            ext.put("phone", m.getPhone());
            return MyMetaOptionItem.builder()
                    .label(m.getNickName())
                    .value(m.getUsername())
                    .ext(ext)
                    .build();
        }).collect(Collectors.toList()), HttpStatus.OK);
    }
}
