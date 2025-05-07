package cn.odboy.core.cache.service;

import cn.odboy.core.service.system.dto.UserJwtVo;
import cn.odboy.core.service.system.dto.UserOnlineVo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface SystemUserOnlineService {
    /**
     * 保存在线用户信息
     *
     * @param userJwtVo /
     * @param token     /
     * @param request   /
     */
    void saveUserJwtModelByToken(UserJwtVo userJwtVo, String token, HttpServletRequest request);


    /**
     * 退出登录
     *
     * @param token /
     */
    void logoutByToken(String token);

    /**
     * 导出
     *
     * @param all      /
     * @param response /
     * @throws IOException /
     */
    void downloadUserOnlineModelExcel(List<UserOnlineVo> all, HttpServletResponse response) throws IOException;


    /**
     * 根据用户名强退用户
     *
     * @param username /
     */
    void kickOutByUsername(String username);
}
