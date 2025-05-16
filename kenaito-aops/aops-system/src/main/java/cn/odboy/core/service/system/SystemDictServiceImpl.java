package cn.odboy.core.service.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.common.util.FileUtil;
import cn.odboy.common.util.PageUtil;
import cn.odboy.core.dal.dataobject.system.DictDO;
import cn.odboy.core.dal.dataobject.system.DictDetailDO;
import cn.odboy.core.dal.mysql.system.DictDetailMapper;
import cn.odboy.core.dal.mysql.system.DictMapper;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.service.system.dto.DictCreateArgs;
import cn.odboy.core.service.system.dto.DictQueryArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

@Service
@RequiredArgsConstructor
public class SystemDictServiceImpl extends ServiceImpl<DictMapper, DictDO> implements SystemDictService {
    private final DictMapper dictMapper;
    private final DictDetailMapper dictDetailMapper;
    private final RedisHelper redisHelper;

    @Override
    public PageResult<DictDO> describeDictPage(DictQueryArgs args, Page<Object> page) {
        IPage<DictDO> dicts = dictMapper.queryDictPageByArgs(args, page);
        return PageUtil.toPage(dicts);
    }

    @Override
    public List<DictDO> describeDictList(DictQueryArgs args) {
        return dictMapper.queryDictPageByArgs(args, PageUtil.getCount(dictMapper)).getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDict(DictCreateArgs args) {
        save(BeanUtil.copyProperties(args, DictDO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyDictById(DictDO resources) {
        // 清理缓存
        delCaches(resources);
        DictDO dictDO = getById(resources.getId());
        dictDO.setName(resources.getName());
        dictDO.setDescription(resources.getDescription());
        saveOrUpdate(dictDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDictByIds(Set<Long> ids) {
        // 清理缓存
        List<DictDO> dictDOS = dictMapper.selectByIds(ids);
        for (DictDO dictDO : dictDOS) {
            delCaches(dictDO);
        }
        // 删除字典
        dictMapper.deleteByIds(ids);
        // 删除字典详情
        dictDetailMapper.deleteDictDetailByDictIds(ids);
    }

    @Override
    public void downloadDictExcel(List<DictDO> dictDOS, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DictDO dictDO : dictDOS) {
            List<DictDetailDO> dictDetailDOS = dictDetailMapper.queryDictDetailListByDictName(dictDO.getName());
            if (CollectionUtil.isNotEmpty(dictDetailDOS)) {
                for (DictDetailDO dictDetailDO : dictDetailDOS) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("字典名称", dictDO.getName());
                    map.put("字典描述", dictDO.getDescription());
                    map.put("字典标签", dictDetailDO.getLabel());
                    map.put("字典值", dictDetailDO.getValue());
                    map.put("创建日期", dictDetailDO.getCreateTime());
                    list.add(map);
                }
            } else {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("字典名称", dictDO.getName());
                map.put("字典描述", dictDO.getDescription());
                map.put("字典标签", null);
                map.put("字典值", null);
                map.put("创建日期", dictDO.getCreateTime());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    public void delCaches(DictDO dictDO) {
        redisHelper.del(RedisKeyConst.DICT_NAME + dictDO.getName());
    }
}
