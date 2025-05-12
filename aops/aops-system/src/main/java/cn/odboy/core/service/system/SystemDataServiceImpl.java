package cn.odboy.core.service.system;

import cn.hutool.core.collection.CollUtil;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.core.constant.DataScopeEnum;
import cn.odboy.core.dal.dataobject.system.DeptDO;
import cn.odboy.core.dal.dataobject.system.RoleDO;
import cn.odboy.core.dal.dataobject.system.UserDO;
import cn.odboy.core.dal.redis.RedisKeyConst;
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
public class SystemDataServiceImpl implements SystemDataService {
    private final RedisHelper redisHelper;
    private final SystemRoleService systemRoleService;
    private final SystemDeptService systemDeptService;

    /**
     * 用户角色和用户部门改变时需清理缓存
     *
     * @param userDO /
     * @return /
     */
    @Override
    public List<Long> describeDeptIdListByUserIdWithDeptId(UserDO userDO) {
        String key = RedisKeyConst.DATA_USER + userDO.getId();
        List<Long> ids = redisHelper.getList(key, Long.class);
        if (CollUtil.isEmpty(ids)) {
            Set<Long> deptIds = new HashSet<>();
            // 查询用户角色
            List<RoleDO> roleDOList = systemRoleService.describeRoleListByUsersId(userDO.getId());
            // 获取对应的部门ID
            for (RoleDO roleDO : roleDOList) {
                DataScopeEnum dataScopeEnum = DataScopeEnum.find(roleDO.getDataScope());
                switch (Objects.requireNonNull(dataScopeEnum)) {
                    case THIS_LEVEL:
                        deptIds.add(userDO.getDept().getId());
                        break;
                    case CUSTOMIZE:
                        deptIds.addAll(getCustomize(deptIds, roleDO));
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
     * @param roleDO    角色
     * @return 数据权限ID
     */
    public Set<Long> getCustomize(Set<Long> deptIds, RoleDO roleDO) {
        Set<DeptDO> deptDOList = systemDeptService.describeDeptByRoleId(roleDO.getId());
        for (DeptDO deptDO : deptDOList) {
            deptIds.add(deptDO.getId());
            List<DeptDO> deptDOChildren = systemDeptService.describeDeptListByPid(deptDO.getId());
            if (CollUtil.isNotEmpty(deptDOChildren)) {
                deptIds.addAll(systemDeptService.describeChildDeptIdListByDeptIds(deptDOChildren));
            }
        }
        return deptIds;
    }
}
