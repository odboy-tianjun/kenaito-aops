package cn.odboy.core.framework.properties.dto;

import lombok.Data;

/**
 * 定时任务线程池 配置
 *
 * @author odboy
 * @date 2025-04-13
 */
@Data
public class QuartzTaskThreadPoolSettingModel {
    /**
     * 核心线程池大小
     */
    private int corePoolSize;
    /**
     * 最大线程数
     */
    private int maxPoolSize;
    /**
     * 活跃时间
     */
    private int keepAliveSeconds;
    /**
     * 队列容量
     */
    private int queueCapacity;
}
