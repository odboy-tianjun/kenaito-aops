package cn.odboy.core.dal.redis.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.BrowserUtil;
import cn.odboy.common.util.DesEncryptUtil;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.IpUtil;
import cn.odboy.common.util.PageUtil;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.framework.permission.core.filter.TokenProvider;
import cn.odboy.core.framework.system.config.AppProperties;
import cn.odboy.core.controller.system.vo.UserJwtVo;
import cn.odboy.core.controller.system.vo.UserOnlineVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemUserOnlineInfoDAO {
    private final RedisHelper redisHelper;
    private final AppProperties appProperties;
    private final TokenProvider tokenProvider;

    /**
     * 查询全部数据
     *
     * @param username /
     * @param pageable /
     * @return /
     */
    public PageResult<UserOnlineVo> describeUserOnlineModelPage(String username, Pageable pageable) {
        List<UserOnlineVo> onlineUserList = describeUserOnlineModelListByUsername(username);
        List<UserOnlineVo> paging = PageUtil.softPaging(pageable.getPageNumber(), pageable.getPageSize(), onlineUserList);
        return PageUtil.toPage(paging, onlineUserList.size());
    }

    /**
     * 查询全部数据，不分页
     *
     * @param username /
     * @return /
     */
    public List<UserOnlineVo> describeUserOnlineModelListByUsername(String username) {
        String loginKey = RedisKeyConst.ONLINE_USER + (StringUtil.isBlank(username) ? "" : "*" + username);
        List<String> keys = redisHelper.scan(loginKey + "*");
        Collections.reverse(keys);
        List<UserOnlineVo> onlineUserList = new ArrayList<>();
        for (String key : keys) {
            onlineUserList.add(redisHelper.get(key, UserOnlineVo.class));
        }
        onlineUserList.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
        return onlineUserList;
    }

    /**
     * 查询用户
     *
     * @param key /
     * @return /
     */
    public UserOnlineVo describeUserOnlineModelByKey(String key) {
        return redisHelper.get(key, UserOnlineVo.class);
    }

    /**
     * 保存在线用户信息
     *
     * @param userJwtVo /
     * @param token     /
     * @param request   /
     */
    public void saveUserJwtModelByToken(UserJwtVo userJwtVo, String token, HttpServletRequest request) {
        String dept = userJwtVo.getUserDO().getDeptDO().getName();
        String ip = BrowserUtil.getIp(request);
        String id = tokenProvider.getId(token);
        String version = BrowserUtil.getVersion(request);
        String address = IpUtil.getCityInfo(ip);
        UserOnlineVo userOnlineVo = null;
        try {
            userOnlineVo = new UserOnlineVo();
            userOnlineVo.setUid(id);
            userOnlineVo.setUserName(userJwtVo.getUsername());
            userOnlineVo.setNickName(userJwtVo.getUserDO().getNickName());
            userOnlineVo.setDept(dept);
            userOnlineVo.setBrowser(version);
            userOnlineVo.setIp(ip);
            userOnlineVo.setAddress(address);
            userOnlineVo.setKey(DesEncryptUtil.desEncrypt(token));
            userOnlineVo.setLoginTime(new Date());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        String loginKey = tokenProvider.loginKey(token);
        redisHelper.set(loginKey, userOnlineVo, appProperties.getJwt().getTokenValidityInSeconds(), TimeUnit.MILLISECONDS);
    }


    /**
     * 退出登录
     *
     * @param token /
     */
    public void logoutByToken(String token) {
        String loginKey = tokenProvider.loginKey(token);
        redisHelper.del(loginKey);
    }

    /**
     * 导出
     *
     * @param all      /
     * @param response /
     * @throws IOException /
     */
    public void downloadUserOnlineModelExcel(List<UserOnlineVo> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserOnlineVo user : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", user.getUserName());
            map.put("部门", user.getDept());
            map.put("登录IP", user.getIp());
            map.put("登录地点", user.getAddress());
            map.put("浏览器", user.getBrowser());
            map.put("登录日期", user.getLoginTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 根据用户名强退用户
     *
     * @param username /
     */
    public void kickOutByUsername(String username) {
        String loginKey = RedisKeyConst.ONLINE_USER + username + "*";
        redisHelper.scanDel(loginKey);
    }
}
