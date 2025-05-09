package cn.odboy.core.controller.system;

import cn.odboy.common.pojo.PageResult;
import cn.odboy.core.dal.redis.system.SystemUserOnlineInfoDAO;
import cn.odboy.core.controller.system.vo.UserOnlineVo;
import cn.odboy.common.util.DesEncryptUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/online")
@Api(tags = "系统：在线用户管理")
public class OnlineController {
    private final SystemUserOnlineInfoDAO systemUserOnlineInfoDAO;

    @ApiOperation("查询在线用户")
    @GetMapping
    @PreAuthorize("@el.check()")
    public ResponseEntity<PageResult<UserOnlineVo>> queryOnlineUser(String username, Pageable pageable) {
        return new ResponseEntity<>(systemUserOnlineInfoDAO.describeUserOnlineModelPage(username, pageable), HttpStatus.OK);
    }

    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check()")
    public void exportOnlineUser(HttpServletResponse response, String username) throws IOException {
        systemUserOnlineInfoDAO.downloadUserOnlineModelExcel(systemUserOnlineInfoDAO.describeUserOnlineModelListByUsername(username), response);
    }

    @ApiOperation("踢出用户")
    @PostMapping(value = "/kickOutUser")
    @PreAuthorize("@el.check()")
    public ResponseEntity<Object> kickOutUser(@RequestBody Set<String> keys) throws Exception {
        for (String token : keys) {
            // 解密Key
            token = DesEncryptUtil.desDecrypt(token);
            systemUserOnlineInfoDAO.logoutByToken(token);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
