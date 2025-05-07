package cn.odboy.core.service.system.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.odboy.core.api.system.SystemDeptApi;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.Dept;
import cn.odboy.core.dal.dataobject.system.User;
import cn.odboy.core.dal.mysql.system.DeptMapper;
import cn.odboy.core.dal.mysql.system.UserMapper;
import cn.odboy.core.service.system.SystemDeptService;
import cn.odboy.core.service.system.dto.CreateDeptRequest;
import cn.odboy.exception.BadRequestException;
import cn.odboy.redis.RedisHelper;
import cn.odboy.util.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemDeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements SystemDeptService {
    private final DeptMapper deptMapper;
    private final UserMapper userMapper;
    private final RedisHelper redisHelper;
    private final SystemDeptApi systemDeptApi;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDept(CreateDeptRequest resources) {
        save(BeanUtil.copyProperties(resources, Dept.class));
        // 清理缓存
        updateSubCnt(resources.getPid());
        // 清理自定义角色权限的DataScope缓存
        delCaches(resources.getPid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyDept(Dept resources) {
        // 旧的部门
        Long oldPid = systemDeptApi.describeDeptById(resources.getId()).getPid();
        Long newPid = resources.getPid();
        if (resources.getPid() != null && resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        Dept dept = getById(resources.getId());
        resources.setId(dept.getId());
        saveOrUpdate(resources);
        // 更新父节点中子节点数目
        updateSubCnt(oldPid);
        updateSubCnt(newPid);
        // 清理缓存
        delCaches(resources.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDeptByIds(Set<Dept> deptSet) {
        for (Dept dept : deptSet) {
            // 清理缓存
            delCaches(dept.getId());
            deptMapper.deleteById(dept.getId());
            updateSubCnt(dept.getPid());
        }
    }

    @Override
    public void downloadDeptExcel(List<Dept> deptList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Dept dept : deptList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部门名称", dept.getName());
            map.put("部门状态", dept.getEnabled() ? "启用" : "停用");
            map.put("创建日期", dept.getCreateTime());
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
        List<User> users = userMapper.queryUserListByDeptId(id);
        // 删除数据权限
        redisHelper.delByKeys(SystemRedisKey.DATA_USER, users.stream().map(User::getId).collect(Collectors.toSet()));
        redisHelper.del(SystemRedisKey.DEPT_ID + id);
    }
}
