package cn.odboy.core.framework.server;

import cn.odboy.core.framework.properties.AppProperties;
import cn.odboy.core.framework.properties.dto.QuartzTaskThreadPoolSettingModel;
import com.alibaba.ttl.TtlRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建自定义的线程池
 */
@EnableAsync
@Configuration
public class AsyncExecutor implements AsyncConfigurer {
    @Autowired
    private AppProperties properties;

    /**
     * 自定义线程池，用法 @Async
     *
     * @return Executor
     */
    @Override
    public Executor getAsyncExecutor() {
        QuartzTaskThreadPoolSettingModel asyncTaskPool = properties.getAsyncTaskPool();
        // 自定义工厂
        ThreadFactory factory = r -> new Thread(r, "default-async-" + new AtomicInteger(1).getAndIncrement());
        // 自定义线程池
        return new ThreadPoolExecutor(asyncTaskPool.getCorePoolSize(), asyncTaskPool.getMaxPoolSize(), asyncTaskPool.getKeepAliveSeconds(),
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(asyncTaskPool.getQueueCapacity()), factory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 自定义线程池，用法，注入到类中使用
     */
    @Bean("taskAsync")
    public ThreadPoolTaskExecutor taskAsync() {
        QuartzTaskThreadPoolSettingModel asyncTaskPool = properties.getAsyncTaskPool();
        // 用法 private ThreadPoolTaskExecutor taskExecutor
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncTaskPool.getCorePoolSize());
        executor.setMaxPoolSize(asyncTaskPool.getMaxPoolSize());
        executor.setQueueCapacity(asyncTaskPool.getQueueCapacity());
        executor.setThreadNamePrefix("task-async-");
        executor.setKeepAliveSeconds(asyncTaskPool.getKeepAliveSeconds());
        // DiscardOldestPolicy，抛弃最早的任务，将新任务加入队列。
        // AbortPolicy，拒绝执行新任务，并抛出异常。
        // CallerRunsPolicy，交由调用者线程执行新任务，如果调用者线程已关闭，则抛弃任务。
        // DiscardPolicy，直接抛弃新任务。
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 设置包装器
        executor.setTaskDecorator(TtlRunnable::get);
        executor.initialize();
        return executor;
    }
}
