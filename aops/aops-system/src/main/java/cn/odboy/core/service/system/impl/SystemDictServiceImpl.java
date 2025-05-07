package cn.odboy.core.service.system.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.Dict;
import cn.odboy.core.dal.dataobject.system.DictDetail;
import cn.odboy.core.dal.mysql.system.DictDetailMapper;
import cn.odboy.core.dal.mysql.system.DictMapper;
import cn.odboy.core.service.system.SystemDictService;
import cn.odboy.core.service.system.dto.CreateDictRequest;
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

@Service
@RequiredArgsConstructor
public class SystemDictServiceImpl extends ServiceImpl<DictMapper, Dict> implements SystemDictService {
    private final DictMapper dictMapper;
    private final DictDetailMapper dictDetailMapper;
    private final RedisHelper redisHelper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDict(CreateDictRequest resources) {
        save(BeanUtil.copyProperties(resources, Dict.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyDictById(Dict resources) {
        // 清理缓存
        delCaches(resources);
        Dict dict = getById(resources.getId());
        dict.setName(resources.getName());
        dict.setDescription(resources.getDescription());
        saveOrUpdate(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDictByIds(Set<Long> ids) {
        // 清理缓存
        List<Dict> dicts = dictMapper.selectByIds(ids);
        for (Dict dict : dicts) {
            delCaches(dict);
        }
        // 删除字典
        dictMapper.deleteByIds(ids);
        // 删除字典详情
        dictDetailMapper.deleteDictDetailByDictIds(ids);
    }

    @Override
    public void downloadDictExcel(List<Dict> dicts, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Dict dict : dicts) {
            List<DictDetail> dictDetails = dictDetailMapper.queryDictDetailListByDictName(dict.getName());
            if (CollectionUtil.isNotEmpty(dictDetails)) {
                for (DictDetail dictDetail : dictDetails) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("字典名称", dict.getName());
                    map.put("字典描述", dict.getDescription());
                    map.put("字典标签", dictDetail.getLabel());
                    map.put("字典值", dictDetail.getValue());
                    map.put("创建日期", dictDetail.getCreateTime());
                    list.add(map);
                }
            } else {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("字典名称", dict.getName());
                map.put("字典描述", dict.getDescription());
                map.put("字典标签", null);
                map.put("字典值", null);
                map.put("创建日期", dict.getCreateTime());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    public void delCaches(Dict dict) {
        redisHelper.del(SystemRedisKey.DICT_NAME + dict.getName());
    }
}