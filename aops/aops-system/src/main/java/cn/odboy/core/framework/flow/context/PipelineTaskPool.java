package cn.odboy.core.framework.flow.context;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流水线任务池
 *
 * @author odboy
 */
public class PipelineTaskPool {
    // 默认线程池参数
    private static final int DEFAULT_CORE_POOL_SIZE = 5;
    private static final int DEFAULT_MAX_POOL_SIZE = 10;
    private static final long DEFAULT_KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    private static final int DEFAULT_QUEUE_CAPACITY = 100;

    // 线程池实例
    private final ThreadPoolExecutor executor;
    // 存储正在运行的任务
    private final Map<String, ManagedTask> runningTasks = new ConcurrentHashMap<>();
    // 线程池管理器名称
    private final String name;

    /**
     * 使用默认参数创建线程池管理器
     */
    public PipelineTaskPool(String name) {
        this(name, DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE,
                DEFAULT_KEEP_ALIVE_TIME, DEFAULT_TIME_UNIT,
                new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY));
    }

    /**
     * 自定义参数创建线程池管理器
     */
    public PipelineTaskPool(String name, int corePoolSize, int maxPoolSize,
                            long keepAliveTime, TimeUnit unit,
                            BlockingQueue<Runnable> workQueue) {
        this.name = name;
        this.executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, name + "-thread-" + threadNumber.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 提交任务并管理
     *
     * @param taskId 任务ID
     * @param task   任务逻辑
     * @return true表示提交成功，false表示任务已存在
     */
    public boolean submitTask(String taskId, Runnable task) {
        if (runningTasks.containsKey(taskId)) {
            return false;
        }

        ManagedTask managedTask = new ManagedTask(taskId, task);
        runningTasks.put(taskId, managedTask);
        executor.execute(managedTask);
        return true;
    }

    /**
     * 正常停止任务
     *
     * @param taskId 任务ID
     * @return true表示停止成功，false表示任务不存在或已停止
     */
    public boolean stopTaskGracefully(String taskId) {
        ManagedTask task = runningTasks.get(taskId);
        if (task != null) {
            return task.stopGracefully();
        }
        return false;
    }

    /**
     * 强制停止任务
     *
     * @param taskId 任务ID
     * @return true表示停止成功，false表示任务不存在
     */
    public boolean stopTaskForcibly(String taskId) {
        ManagedTask task = runningTasks.remove(taskId);
        if (task != null) {
            return task.stopForcibly();
        }
        return false;
    }

    /**
     * 检查任务是否正在运行
     *
     * @param taskId 任务ID
     * @return true表示正在运行
     */
    public boolean isTaskRunning(String taskId) {
        ManagedTask task = runningTasks.get(taskId);
        return task != null && !task.isStopped();
    }

    /**
     * 获取正在运行的任务数量
     *
     * @return 运行中任务数
     */
    public int getRunningTaskCount() {
        return runningTasks.size();
    }

    /**
     * 关闭线程池（等待所有任务完成）
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * 立即关闭线程池（尝试停止所有任务）
     */
    public void shutdownNow() {
        executor.shutdownNow();
        runningTasks.clear();
    }

    /**
     * 获取线程池状态信息
     *
     * @return 状态信息字符串
     */
    public String getPoolStatus() {
        return String.format(
                "[%s] PoolStatus: Active=%d, Completed=%d, Task=%d, Queue=%d/%d",
                name,
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount(),
                executor.getQueue().size(),
                executor.getQueue().remainingCapacity()
        );
    }

    /**
     * 被管理的任务包装类
     */
    private class ManagedTask implements Runnable {
        private final String taskId;
        private final Runnable task;
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final AtomicBoolean stopped = new AtomicBoolean(false);
        private Thread currentThread;

        public ManagedTask(String taskId, Runnable task) {
            this.taskId = taskId;
            this.task = task;
        }

        @Override
        public void run() {
            currentThread = Thread.currentThread();
            try {
                // 执行前检查是否已被停止
                if (running.get()) {
                    task.run();
                }
            } finally {
                // 任务完成后从运行列表中移除
                runningTasks.remove(taskId);
                stopped.set(true);
            }
        }

        /**
         * 正常停止任务
         *
         * @return 是否成功停止
         */
        public boolean stopGracefully() {
            if (running.compareAndSet(true, false)) {
                return true;
            }
            return false;
        }

        /**
         * 强制停止任务
         *
         * @return 是否成功停止
         */
        public boolean stopForcibly() {
            if (stopped.get()) {
                return false;
            }

            if (running.compareAndSet(true, false)) {
                if (currentThread != null) {
                    // 暴力停止线程
                    // currentThread.stop();
                    try {
                        /**
                         * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
                         * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身；
                         * 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
                         */
                        currentThread.interrupt();
                        currentThread.join();
                    } catch (InterruptedException e) {
                        currentThread.interrupt();
                    } catch (Exception e) {
                        // ignore
                        // currentThread.stop();
                    }
                }
                return true;
            }
            return false;
        }

        /**
         * 检查任务是否已停止
         *
         * @return true表示已停止
         */
        public boolean isStopped() {
            return stopped.get();
        }
    }
}
