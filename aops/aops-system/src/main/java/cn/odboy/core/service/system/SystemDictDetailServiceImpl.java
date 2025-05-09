package cn.odboy.core.service.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.odboy.common.pojo.PageResult;
import cn.odboy.common.util.PageUtil;
import cn.odboy.core.dal.redis.RedisKeyConst;
import cn.odboy.core.dal.dataobject.system.Dict;
import cn.odboy.core.dal.dataobject.system.DictDetail;
import cn.odboy.core.dal.mysql.system.DictDetailMapper;
import cn.odboy.core.dal.mysql.system.DictMapper;
import cn.odboy.core.service.system.dto.CreateDictDetailRequest;
import cn.odboy.common.redis.RedisHelper;
import cn.odboy.core.service.system.dto.QueryDictDetailRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SystemDictDetailServiceImpl extends ServiceImpl<DictDetailMapper, DictDetail> implements SystemDictDetailService {
    private final DictMapper dictMapper;
    private final RedisHelper redisHelper;
    private final DictDetailMapper dictDetailMapper;
    @Override
    public PageResult<DictDetail> describeDictDetailPage(QueryDictDetailRequest criteria, Page<Object> page) {
        return PageUtil.toPage(dictDetailMapper.queryDictDetailPageByArgs(criteria, page));
    }
    @Override
    public List<DictDetail> describeDictDetailListByName(String name) {
        String key = RedisKeyConst.DICT_NAME + name;
        List<DictDetail> dictDetails = redisHelper.getList(key, DictDetail.class);
        if (CollUtil.isEmpty(dictDetails)) {
            dictDetails = dictDetailMapper.queryDictDetailListByDictName(name);
            redisHelper.set(key, dictDetails, 1, TimeUnit.DAYS);
        }
        return dictDetails;
    }

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
        redisHelper.del(RedisKeyConst.DICT_NAME + dict.getName());
    }
}