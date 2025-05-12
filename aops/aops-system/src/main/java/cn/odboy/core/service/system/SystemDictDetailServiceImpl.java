package cn.odboy.core.service.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.common.util.PageUtil;
import cn.odboy.core.dal.dataobject.system.DictDO;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.dal.dataobject.system.DictDetailDO;
import cn.odboy.core.dal.mysql.system.DictDetailMapper;
import cn.odboy.core.dal.mysql.system.DictMapper;
import cn.odboy.core.service.system.dto.CreateDictDetailArgs;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.core.service.system.dto.QueryDictDetailArgs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SystemDictDetailServiceImpl extends ServiceImpl<DictDetailMapper, DictDetailDO> implements SystemDictDetailService {
    private final DictMapper dictMapper;
    private final RedisHelper redisHelper;
    private final DictDetailMapper dictDetailMapper;
    @Override
    public PageResult<DictDetailDO> describeDictDetailPage(QueryDictDetailArgs args, Page<Object> page) {
        return PageUtil.toPage(dictDetailMapper.queryDictDetailPageByArgs(args, page));
    }
    @Override
    public List<DictDetailDO> describeDictDetailListByName(String name) {
        String key = RedisKeyConst.DICT_NAME + name;
        List<DictDetailDO> dictDetailDOS = redisHelper.getList(key, DictDetailDO.class);
        if (CollUtil.isEmpty(dictDetailDOS)) {
            dictDetailDOS = dictDetailMapper.queryDictDetailListByDictName(name);
            redisHelper.set(key, dictDetailDOS, 1, TimeUnit.DAYS);
        }
        return dictDetailDOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDictDetail(CreateDictDetailArgs args) {
        DictDetailDO dictDetailDO = BeanUtil.copyProperties(args, DictDetailDO.class);
        dictDetailDO.setDictId(args.getDict().getId());
        save(dictDetailDO);
        // 清理缓存
        delCaches(dictDetailDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyDictDetailById(DictDetailDO resources) {
        DictDetailDO dictDetailDO = getById(resources.getId());
        resources.setId(dictDetailDO.getId());
        // 更新数据
        saveOrUpdate(resources);
        // 清理缓存
        delCaches(dictDetailDO);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDictDetailById(Long id) {
        DictDetailDO dictDetailDO = getById(id);
        removeById(id);
        // 清理缓存
        delCaches(dictDetailDO);
    }

    public void delCaches(DictDetailDO dictDetailDO) {
        DictDO dictDO = dictMapper.selectById(dictDetailDO.getDictId());
        redisHelper.del(RedisKeyConst.DICT_NAME + dictDO.getName());
    }
}