package cn.odboy.core.service.system.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.odboy.core.constant.SystemRedisKey;
import cn.odboy.core.dal.dataobject.system.Dict;
import cn.odboy.core.dal.dataobject.system.DictDetail;
import cn.odboy.core.dal.mysql.system.DictDetailMapper;
import cn.odboy.core.dal.mysql.system.DictMapper;
import cn.odboy.core.service.system.SystemDictDetailService;
import cn.odboy.core.service.system.dto.CreateDictDetailRequest;
import cn.odboy.redis.RedisHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemDictDetailServiceImpl extends ServiceImpl<DictDetailMapper, DictDetail> implements SystemDictDetailService {
    private final DictMapper dictMapper;
    private final RedisHelper redisHelper;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDictDetail(CreateDictDetailRequest resources) {
        DictDetail dictDetail = BeanUtil.copyProperties(resources, DictDetail.class);
        dictDetail.setDictId(resources.getDict().getId());
        save(dictDetail);
        // 清理缓存
        delCaches(dictDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyDictDetailById(DictDetail resources) {
        DictDetail dictDetail = getById(resources.getId());
        resources.setId(dictDetail.getId());
        // 更新数据
        saveOrUpdate(resources);
        // 清理缓存
        delCaches(dictDetail);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDictDetailById(Long id) {
        DictDetail dictDetail = getById(id);
        removeById(id);
        // 清理缓存
        delCaches(dictDetail);
    }

    public void delCaches(DictDetail dictDetail) {
        Dict dict = dictMapper.selectById(dictDetail.getDictId());
        redisHelper.del(SystemRedisKey.DICT_NAME + dict.getName());
    }
}