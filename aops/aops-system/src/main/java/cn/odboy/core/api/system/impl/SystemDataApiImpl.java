package cn.odboy.core.api.system.impl;

import cn.hutool.core.collection.CollUtil;
import cn.odboy.core.api.system.SystemDataApi;
import cn.odboy.core.api.system.SystemDeptApi;
import cn.odboy.core.api.system.SystemRoleApi;
import cn.odboy.core.constant.DataScopeEnum;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.Dept;
import cn.odboy.core.dal.dataobject.system.Role;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.redis.RedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 数据权限服务实现
 */
@Service
@RequiredArgsConstructor
public class SystemDataApiImpl implements SystemDataApi {
    private final RedisHelper redisHelper;
    private final SystemRoleApi systemRoleApi;
    private final SystemDeptApi systemDeptApi;

    /**
     * 用户角色和用户部门改变时需清理缓存
     *
     * @param user /
     * @return /
     */
    @Override
    public List<Long> describeDeptIdListByUserIdWithDeptId(User user) {
        String key = SystemRedisKey.DATA_USER + user.getId();
        List<Long> ids = redisHelper.getList(key, Long.class);
        if (CollUtil.isEmpty(ids)) {
            Set<Long> deptIds = new HashSet<>();
            // 查询用户角色
            List<Role> roleList = systemRoleApi.describeRoleListByUsersId(user.getId());
            // 获取对应的部门ID
            for (Role role : roleList) {
                DataScopeEnum dataScopeEnum = DataScopeEnum.find(role.getDataScope());
                switch (Objects.requireNonNull(dataScopeEnum)) {
                    case THIS_LEVEL:
                        deptIds.add(user.getDept().getId());
                        break;
                    case CUSTOMIZE:
                        deptIds.addAll(getCustomize(deptIds, role));
                        break;
                    default:
                        return new ArrayList<>();
                }
            }
            ids = new ArrayList<>(deptIds);
            redisHelper.set(key, ids, 1, TimeUnit.DAYS);
        }
        return ids;
    }

    /**
     * 获取自定义的数据权限
     *
     * @param deptIds 部门ID
     * @param role    角色
     * @return 数据权限ID
     */
    public Set<Long> getCustomize(Set<Long> deptIds, Role role) {
        Set<Dept> deptList = systemDeptApi.describeDeptByRoleId(role.getId());
        for (Dept dept : deptList) {
            deptIds.add(dept.getId());
            List<Dept> deptChildren = systemDeptApi.describeDeptListByPid(dept.getId());
            if (CollUtil.isNotEmpty(deptChildren)) {
                deptIds.addAll(systemDeptApi.describeChildDeptIdListByDeptIds(deptChildren));
            }
        }
        return deptIds;
    }
}
