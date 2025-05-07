package cn.odboy.core.framework.permission.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.odboy.constant.SystemConst;
import cn.odboy.context.SpringBeanHolder;
import cn.odboy.core.constant.DataScopeEnum;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 获取当前登录的用户
 */
@Slf4j
@Component
public class SecurityHelper {
    /**
     * 获取当前登录的用户
     *
     * @return UserDetails
     */
    public static UserDetails getCurrentUser() {
        UserDetailsService userDetailsService = SpringBeanHolder.getBean(UserDetailsService.class);
        return userDetailsService.loadUserByUsername(getCurrentUsername());
    }

    /**
     * 获取当前用户的数据权限
     *
     * @return /
     */
    public static List<Long> getCurrentUserDataScope() {
        UserDetails userDetails = getCurrentUser();
        // 将 Java 对象转换为 JSONObject 对象
        JSONObject jsonObject = (JSONObject) JSON.toJSON(userDetails);
        JSONArray jsonArray = jsonObject.getJSONArray("dataScopes");
        return JSON.parseArray(jsonArray.toJSONString(), Long.class);
    }

    /**
     * 获取数据权限级别
     *
     * @return 级别
     */
    public static String getDataScopeType() {
        List<Long> dataScopes = getCurrentUserDataScope();
        if (CollUtil.isEmpty(dataScopes)) {
            return "";
        }
        return DataScopeEnum.ALL.getValue();
    }

    /**
     * 获取用户ID
     *
     * @return 系统用户ID
     */
    public static Long getCurrentUserId() {
        return getCurrentUserId(getToken());
    }

    /**
     * 获取用户ID
     *
     * @return 系统用户ID
     */
    public static Long getCurrentUserId(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return Long.valueOf(jwt.getPayload("userId").toString());
    }

    /**
     * 获取系统用户名称
     *
     * @return 系统用户名称
     */
    public static String getCurrentUsername() {
        return getCurrentUsername(getToken());
    }

    /**
     * 获取系统用户名称
     *
     * @return 系统用户名称
     */
    public static String getCurrentUsername(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt.getPayload("sub").toString();
    }

    /**
     * 获取Token
     *
     * @return /
     */
    public static String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();
        String bearerToken = request.getHeader(SystemConst.HEADER_NAME);
        if (bearerToken != null && bearerToken.startsWith(SystemConst.TOKEN_PREFIX)) {
            // 去掉令牌前缀
            return bearerToken.replace(SystemConst.TOKEN_PREFIX, "");
        } else {
            log.debug("非法Token：{}", bearerToken);
        }
        return null;
    }
}
