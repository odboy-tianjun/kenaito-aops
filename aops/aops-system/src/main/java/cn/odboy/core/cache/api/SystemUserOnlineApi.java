package cn.odboy.core.cache.api;

import cn.odboy.base.PageResult;
import cn.odboy.core.service.system.dto.UserOnlineVo;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SystemUserOnlineApi {

    /**
     * 查询全部数据
     *
     * @param username /
     * @param pageable /
     * @return /
     */
    PageResult<UserOnlineVo> describeUserOnlineModelPage(String username, Pageable pageable);

    /**
     * 查询全部数据，不分页
     *
     * @param username /
     * @return /
     */
    List<UserOnlineVo> describeUserOnlineModelListByUsername(String username);

    /**
     * 查询用户
     *
     * @param key /
     * @return /
     */
    UserOnlineVo describeUserOnlineModelByKey(String key);
}
