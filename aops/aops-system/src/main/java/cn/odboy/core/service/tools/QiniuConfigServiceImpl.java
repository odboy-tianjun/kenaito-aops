package cn.odboy.core.service.tools;

import cn.odboy.core.constant.TransferProtocolConst;
import cn.odboy.core.dal.dataobject.tools.QiniuConfigDO;
import cn.odboy.core.dal.mysql.tools.QiniuConfigMapper;
import cn.odboy.common.exception.BadRequestException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "qiNiu")
public class QiniuConfigServiceImpl extends ServiceImpl<QiniuConfigMapper, QiniuConfigDO> implements QiniuConfigService {
    @Override
    @Cacheable(key = "'config'")
    public QiniuConfigDO describeQiniuConfig() {
        QiniuConfigDO qiniuConfigDO = getById(1L);
        return qiniuConfigDO == null ? new QiniuConfigDO() : qiniuConfigDO;
    }

    @Override
    @CacheEvict(key = "'config'")
    @Transactional(rollbackFor = Exception.class)
    public void saveQiniuConfig(QiniuConfigDO qiniuConfigDO) {
        qiniuConfigDO.setId(1L);
        if (!(qiniuConfigDO.getHost().toLowerCase().startsWith(TransferProtocolConst.PREFIX_HTTP) || qiniuConfigDO.getHost().toLowerCase().startsWith(TransferProtocolConst.PREFIX_HTTPS))) {
            throw new BadRequestException(TransferProtocolConst.PREFIX_HTTPS_BAD_REQUEST);
        }
        saveOrUpdate(qiniuConfigDO);
    }

    @Override
    @CacheEvict(key = "'config'")
    @Transactional(rollbackFor = Exception.class)
    public void modifyQiniuConfigType(String type) {
        QiniuConfigDO qiniuConfigDO = getById(1L);
        qiniuConfigDO.setType(type);
        saveOrUpdate(qiniuConfigDO);
    }
}
