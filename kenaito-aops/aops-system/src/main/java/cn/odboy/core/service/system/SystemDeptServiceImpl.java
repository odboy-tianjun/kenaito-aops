package cn.odboy.core.service.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.model.BaseResult;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.ClassUtil;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.StringUtil;
import cn.odboy.core.constant.DataScopeEnum;
import cn.odboy.core.dal.dataobject.system.DeptDO;
import cn.odboy.core.dal.dataobject.system.UserDO;
import cn.odboy.core.dal.mysql.system.DeptMapper;
import cn.odboy.core.dal.mysql.system.RoleMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.framework.permission.core.util.SecurityHelper;
import cn.odboy.core.service.system.dto.DeptCreateArgs;
import cn.odboy.core.service.system.dto.DeptQueryArgs;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemDeptServiceImpl extends ServiceImpl<DeptMapper, DeptDO> implements SystemDeptService {
    private final DeptMapper deptMapper;
    private final UserMapper userMapper;
    private final RedisHelper redisHelper;
    private final RoleMapper roleMapper;

    /**
     * 筛选出 list 中没有父部门（即 pid 不在其他部门 id 列表中的部门）的部门列表
     */
    private List<DeptDO> deduplication(List<DeptDO> list) {
        List<DeptDO> deptDOList = new ArrayList<>();
        // 使用 Set 存储所有部门的 id
        Set<Long> idSet = list.stream().map(DeptDO::getId).collect(Collectors.toSet());
        // 遍历部门列表，筛选出没有父部门的部门
        for (DeptDO deptDO : list) {
            if (!idSet.contains(deptDO.getPid())) {
                deptDOList.add(deptDO);
            }
        }
        return deptDOList;
    }

    @Override
    public List<DeptDO> describeDeptList(DeptQueryArgs args, Boolean isQuery) throws Exception {
        String dataScopeType = SecurityHelper.getDataScopeType();
        if (isQuery) {
            if (dataScopeType.equals(DataScopeEnum.ALL.getValue())) {
                args.setPidIsNull(true);
            }
            List<Field> fields = ClassUtil.getAllFields(args.getClass(), new ArrayList<>());
            List<String> fieldNames = new ArrayList<>() {{
                add("pidIsNull");
                add("enabled");
            }};
            for (Field field : fields) {
                // 设置对象的访问权限，保证对private的属性的访问
                field.setAccessible(true);
                Object val = field.get(args);
                if (fieldNames.contains(field.getName())) {
                    continue;
                }
                if (ObjectUtil.isNotNull(val)) {
                    args.setPidIsNull(null);
                    break;
                }
            }
        }
        // 数据权限
        args.setIds(SecurityHelper.getCurrentUserDataScope());
        List<DeptDO> list = deptMapper.queryDeptListByArgs(args);
        // 如果为空，就代表为自定义权限或者本级权限，就需要去重，不理解可以注释掉，看查询结果
        if (StringUtil.isBlank(dataScopeType)) {
            return deduplication(list);
        }
        return list;
    }

    @Override
    public DeptDO describeDeptById(Long id) {
        String key = RedisKeyConst.DEPT_ID + id;
        DeptDO deptDO = redisHelper.get(key, DeptDO.class);
        if (deptDO == null) {
            deptDO = deptMapper.selectById(id);
            redisHelper.set(key, deptDO, 1, TimeUnit.DAYS);
        }
        return deptDO;
    }

    @Override
    public List<DeptDO> describeDeptListByPid(long pid) {
        return deptMapper.queryDeptListByPid(pid);
    }

    @Override
    public Set<DeptDO> describeDeptByRoleId(Long id) {
        return deptMapper.queryDeptSetByRoleId(id);
    }

    @Override
    public Set<DeptDO> describeRelationDeptSet(List<DeptDO> menuList, Set<DeptDO> deptDOSet) {
        for (DeptDO deptDO : menuList) {
            deptDOSet.add(deptDO);
            List<DeptDO> deptDOList = deptMapper.queryDeptListByPid(deptDO.getId());
            if (CollUtil.isNotEmpty(deptDOList)) {
                describeRelationDeptSet(deptDOList, deptDOSet);
            }
        }
        return deptDOSet;
    }

    @Override
    public List<Long> describeChildDeptIdListByDeptIds(List<DeptDO> deptDOList) {
        List<Long> list = new ArrayList<>();
        deptDOList.forEach(dept -> {
                    if (dept != null && dept.getEnabled()) {
                        List<DeptDO> deptDOList1 = deptMapper.queryDeptListByPid(dept.getId());
                        if (CollUtil.isNotEmpty(deptDOList1)) {
                            list.addAll(describeChildDeptIdListByDeptIds(deptDOList1));
                        }
                        list.add(dept.getId());
                    }
                }
        );
        return list;
    }

    @Override
    public List<DeptDO> describeSuperiorDeptListByPid(DeptDO deptDO, List<DeptDO> deptDOList) {
        if (deptDO.getPid() == null) {
            deptDOList.addAll(deptMapper.queryDeptListByPidIsNull());
            return deptDOList;
        }
        deptDOList.addAll(deptMapper.queryDeptListByPid(deptDO.getPid()));
        return describeSuperiorDeptListByPid(describeDeptById(deptDO.getPid()), deptDOList);
    }

    @Override
    public BaseResult<Object> buildDeptTree(List<DeptDO> deptDOList) {
        Set<DeptDO> trees = new LinkedHashSet<>();
        Set<DeptDO> deptDOSet = new LinkedHashSet<>();
        List<String> deptNames = deptDOList.stream().map(DeptDO::getName).collect(Collectors.toList());
        boolean isChild;
        for (DeptDO deptDO : deptDOList) {
            isChild = false;
            if (deptDO.getPid() == null) {
                trees.add(deptDO);
            }
            for (DeptDO it : deptDOList) {
                if (it.getPid() != null && deptDO.getId().equals(it.getPid())) {
                    isChild = true;
                    if (deptDO.getChildren() == null) {
                        deptDO.setChildren(new ArrayList<>());
                    }
                    deptDO.getChildren().add(it);
                }
            }
            if (isChild) {
                deptDOSet.add(deptDO);
            } else if (deptDO.getPid() != null && !deptNames.contains(describeDeptById(deptDO.getPid()).getName())) {
                deptDOSet.add(deptDO);
            }
        }
        if (CollectionUtil.isEmpty(trees)) {
            trees = deptDOSet;
        }
        BaseResult<Object> baseResult = new BaseResult<>();
        baseResult.setContent(CollectionUtil.isEmpty(trees) ? deptDOSet : trees);
        baseResult.setTotalElements(deptDOSet.size());
        return baseResult;
    }

    @Override
    public void verifyBindRelationByIds(Set<DeptDO> deptDOSet) {
        Set<Long> deptIds = deptDOSet.stream().map(DeptDO::getId).collect(Collectors.toSet());
        if (userMapper.getUserCountByDeptIds(deptIds) > 0) {
            throw new BadRequestException("所选部门存在用户关联，请解除后再试！");
        }
        if (roleMapper.getRoleCountByDeptIds(deptIds) > 0) {
            throw new BadRequestException("所选部门存在角色关联，请解除后再试！");
        }
    }

    @Override
    public void traverseDeptByIdWithPids(Set<Long> ids, Set<DeptDO> deptDOS) {
        for (Long id : ids) {
            // 根部门
            deptDOS.add(describeDeptById(id));
            // 子部门
            List<DeptDO> deptDOList = describeDeptListByPid(id);
            if (CollectionUtil.isNotEmpty(deptDOList)) {
                deptDOS = describeRelationDeptSet(deptDOList, deptDOS);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDept(DeptCreateArgs args) {
        save(BeanUtil.copyProperties(args, DeptDO.class));
        // 清理缓存
        updateSubCnt(args.getPid());
        // 清理自定义角色权限的DataScope缓存
        delCaches(args.getPid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyDept(DeptDO resources) {
        // 旧的部门
        Long oldPid = describeDeptById(resources.getId()).getPid();
        Long newPid = resources.getPid();
        if (resources.getPid() != null && resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        DeptDO deptDO = getById(resources.getId());
        resources.setId(deptDO.getId());
        saveOrUpdate(resources);
        // 更新父节点中子节点数目
        updateSubCnt(oldPid);
        updateSubCnt(newPid);
        // 清理缓存
        delCaches(resources.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDeptByIds(Set<DeptDO> deptDOSet) {
        for (DeptDO deptDO : deptDOSet) {
            // 清理缓存
            delCaches(deptDO.getId());
            deptMapper.deleteById(deptDO.getId());
            updateSubCnt(deptDO.getPid());
        }
    }

    @Override
    public void downloadDeptExcel(List<DeptDO> deptDOList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeptDO deptDO : deptDOList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部门名称", deptDO.getName());
            map.put("部门状态", deptDO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", deptDO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    private void updateSubCnt(Long deptId) {
        if (deptId != null) {
            int count = deptMapper.getDeptCountByPid(deptId);
            deptMapper.updateSubCountById(count, deptId);
        }
    }


    /**
     * 清理缓存
     *
     * @param id /
     */
    public void delCaches(Long id) {
        List<UserDO> userDOS = userMapper.queryUserListByDeptId(id);
        // 删除数据权限
        redisHelper.delByKeys(RedisKeyConst.DATA_USER, userDOS.stream().map(UserDO::getId).collect(Collectors.toSet()));
        redisHelper.del(RedisKeyConst.DEPT_ID + id);
    }
}
