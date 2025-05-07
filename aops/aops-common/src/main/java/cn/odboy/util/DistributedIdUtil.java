package cn.odboy.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 分布式id生成工具
 *
 * @author odboy
 * @date 2025-04-05
 */
public final class DistributedIdUtil {
    /**
     * 起始毫秒时间戳，2020-12-30
     */
    private static final long START_TIMESTAMP = 1609314517000L;
    /**
     * 数据中心 ID 所占位数
     */
    private final long dataCenterIdBits;
    /**
     * 机器 ID 所占位数
     */
    private final long workerIdBits;
    /**
     * 序列号所占位数
     */
    private final long sequenceBits;

    /**
     * 数据中心 ID 最大值
     */
    private final long maxDataCenterId;
    /**
     * 机器 ID 最大值
     */
    private final long maxWorkerId;
    /**
     * 序列号最大值
     */
    private final long sequenceMask;

    /**
     * 机器 ID 向左移位数
     */
    private final long workerIdShift;
    /**
     * 数据中心 ID 向左移位数
     */
    private final long dataCenterIdShift;
    /**
     * 时间戳向左移位数
     */
    private final long timestampLeftShift;

    /**
     * 数据中心 ID
     */
    private final long dataCenterId;
    /**
     * 机器 ID
     */
    private final long workerId;
    /**
     * 序列号
     */
    private final AtomicLong sequence = new AtomicLong(0L);
    /**
     * 上一次生成 ID 的时间戳
     */
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);

    /**
     * 单例实例持有者类
     */
    private static class InstanceHolder {
        private static final DistributedIdUtil INSTANCE = new DistributedIdUtil(20, 12, 5, 5, 12);
    }

    /**
     * 构造函数，初始化数据中心 ID 和机器 ID
     *
     * @param dataCenterId     数据中心 ID
     * @param workerId         机器 ID
     * @param dataCenterIdBits 数据中心 ID 所占位数
     * @param workerIdBits     机器 ID 所占位数
     * @param sequenceBits     序列号所占位数
     */
    public DistributedIdUtil(long dataCenterId, long workerId, long dataCenterIdBits, long workerIdBits, long sequenceBits) {
        this.dataCenterIdBits = dataCenterIdBits;
        this.workerIdBits = workerIdBits;
        this.sequenceBits = sequenceBits;

        this.maxDataCenterId = ~(-1L << dataCenterIdBits);
        this.maxWorkerId = ~(-1L << workerIdBits);
        this.sequenceMask = ~(-1L << sequenceBits);

        this.workerIdShift = sequenceBits;
        this.dataCenterIdShift = sequenceBits + workerIdBits;
        this.timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException("Data center ID can't be greater than " + maxDataCenterId + " or less than 0");
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Worker ID can't be greater than " + maxWorkerId + " or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    /**
     * 生成下一个分布式 ID
     *
     * @return 生成的分布式 ID
     */
    public long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // 如果当前时间戳小于上一次生成 ID 的时间戳，说明时钟回拨，抛出异常
        if (currentTimestamp < lastTimestamp.get()) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp.get() - currentTimestamp) + " milliseconds");
        }

        long lastTimestampValue = lastTimestamp.get();
        if (currentTimestamp == lastTimestampValue) {
            // 同一毫秒内，序列号自增
            long seq = sequence.incrementAndGet() & sequenceMask;
            if (seq == 0) {
                // 序列号溢出，等待下一毫秒
                currentTimestamp = waitNextMillis(lastTimestampValue);
            }
            return ((currentTimestamp - START_TIMESTAMP) << timestampLeftShift) |
                    (dataCenterId << dataCenterIdShift) |
                    (workerId << workerIdShift) |
                    seq;
        } else {
            // 时间戳改变，重置序列号
            sequence.set(0L);
            lastTimestamp.set(currentTimestamp);
            return ((currentTimestamp - START_TIMESTAMP) << timestampLeftShift) |
                    (dataCenterId << dataCenterIdShift) |
                    (workerId << workerIdShift);
        }
    }

    /**
     * 等待下一毫秒
     *
     * @param lastTimestamp 上一次生成 ID 的时间戳
     * @return 当前时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 获取单例实例的下一个分布式 ID
     *
     * @return 生成的分布式 ID
     */
    public static long fastNextId() {
        return InstanceHolder.INSTANCE.nextId();
    }

    /**
     * 主方法，用于测试分布式 ID 生成
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                long id = Thread.currentThread().getId();
                for (int j = 0; j < 20; j++) {
                    System.out.println(id + " - " + DistributedIdUtil.fastNextId());
                }
            }).start();
        }
    }
}
