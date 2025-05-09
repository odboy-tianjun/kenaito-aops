package cn.odboy.core.service.system;


import cn.odboy.core.dal.dataobject.system.User;
import java.util.List;

/**
 * 数据权限服务类
 */
public interface SystemDataService {

    /**
     * 获取数据权限
     *
     * @param user /
     * @return /
     */
    List<Long> describeDeptIdListByUserIdWithDeptId(User user);
}
