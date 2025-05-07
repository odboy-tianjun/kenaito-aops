package cn.odboy.core.controller.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.odboy.annotation.AnonymousPostMapping;
import cn.odboy.core.framework.properties.AppProperties;
import cn.odboy.core.constant.LoginCodeEnum;
import cn.odboy.constant.SystemConst;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.framework.permission.util.SecurityHelper;
import cn.odboy.core.service.system.dto.UserJwtVo;
import cn.odboy.core.service.system.dto.UserLoginRequest;
import cn.odboy.core.dal.model.UserInfoResponse;
import cn.odboy.core.framework.permission.TokenProvider;
import cn.odboy.core.framework.permission.UserDetailsServiceImpl;
import cn.odboy.core.cache.service.impl.SystemUserOnlineServiceImpl;
import cn.odboy.exception.BadRequestException;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.RsaEncryptUtil;
import cn.odboy.util.StringUtil;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "系统：系统授权接口")
public class AuthController {
    private final RedisHelper redisHelper;
    private final SystemUserOnlineServiceImpl onlineUserService;
    private final TokenProvider tokenProvider;
    private final AppProperties properties;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    @ApiOperation("登录授权")
    @AnonymousPostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody UserLoginRequest loginRequest, HttpServletRequest request) throws Exception {
        // 密码解密
        String password = RsaEncryptUtil.decryptByPrivateKey(properties.getRsa().getPrivateKey(), loginRequest.getPassword());
        // 查询验证码
        String code = redisHelper.get(loginRequest.getUuid(), String.class);
        // 清除验证码
        redisHelper.del(loginRequest.getUuid());
        if (StringUtil.isBlank(code)) {
            throw new BadRequestException("验证码不存在或已过期");
        }
        if (StringUtil.isBlank(loginRequest.getCode()) || !loginRequest.getCode().equalsIgnoreCase(code)) {
            throw new BadRequestException("验证码错误");
        }
        // 获取用户信息
        UserJwtVo jwtUser = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        // 验证用户密码
        if (!passwordEncoder.matches(password, jwtUser.getPassword())) {
            throw new BadRequestException("登录密码错误");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成令牌
        String token = tokenProvider.createToken(jwtUser);
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<>(2) {{
            put("token", String.format("%s %s", SystemConst.TOKEN_PREFIX, token));
            put("user", BeanUtil.copyProperties(jwtUser, UserInfoResponse.class));
        }};
        if (properties.getLogin().isSingle()) {
            // 踢掉之前已经登录的token
            onlineUserService.kickOutByUsername(loginRequest.getUsername());
        }
        // 保存在线信息
        onlineUserService.saveUserJwtModelByToken(jwtUser, token, request);
        // 返回登录信息
        return ResponseEntity.ok(authInfo);
    }

    @ApiOperation("获取用户信息")
    @PostMapping(value = "/info")
    public ResponseEntity<UserInfoResponse> getUserInfo() {
        UserJwtVo jwtUser = (UserJwtVo) SecurityHelper.getCurrentUser();
        UserInfoResponse userInfoResponse = BeanUtil.copyProperties(jwtUser, UserInfoResponse.class);
        return ResponseEntity.ok(userInfoResponse);
    }

    @ApiOperation("获取验证码")
    @AnonymousPostMapping(value = "/code")
    public ResponseEntity<Object> getCode() {
        // 获取运算的结果
        Captcha captcha = properties.getLogin().getCaptchaSetting().getCaptcha();
        String uuid = SystemRedisKey.CAPTCHA_LOGIN + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(SystemConst.SYMBOL_DOT)) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        redisHelper.set(uuid, captchaValue, properties.getLogin().getCaptchaSetting().getExpiration(), TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(imgResult);
    }

    @ApiOperation("退出登录")
    @AnonymousPostMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        String token = tokenProvider.getToken(request);
        onlineUserService.logoutByToken(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
