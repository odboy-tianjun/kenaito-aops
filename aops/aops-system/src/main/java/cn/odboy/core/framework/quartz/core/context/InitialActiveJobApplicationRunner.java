package cn.odboy.core.framework.quartz.core.context;

import cn.odboy.core.dal.dataobject.job.QuartzJobDO;
import cn.odboy.core.dal.mysql.job.QuartzJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialActiveJobApplicationRunner implements ApplicationRunner {
    private final QuartzJobMapper quartzJobMapper;
    private final QuartzManage quartzManage;

    /**
     * 项目启动时重新激活启用的定时任务
     *
     * @param applicationArguments /
     */
    @Override
    public void run(ApplicationArguments applicationArguments) {
        List<QuartzJobDO> quartzJobDOS = quartzJobMapper.queryActiveQuartzJobList();
        quartzJobDOS.forEach(quartzManage::addJob);
        log.info("Timing task injection complete");
    }
}
