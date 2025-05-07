package cn.odboy.core.framework.flow.context;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.odboy.context.SpringBeanHolder;
import cn.odboy.core.framework.flow.dto.PipelineNodeVo;
import cn.odboy.core.framework.flow.handler.SerialPipelineNodeHandler;
import cn.odboy.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 流水线进程管理工具
 *
 * @author odboy
 */
@Slf4j
@Component
public class PipelineProcessManager {
    // 使用ConcurrentHashMap保证线程安全
    private final Map<String, Thread> runningTasks = new ConcurrentHashMap<>();
    // 使用AtomicBoolean作为停止标志，保证原子性操作
    private final Map<String, AtomicBoolean> stopFlags = new ConcurrentHashMap<>();
    // 常量定义
    private static final String PIPELINE_THREAD_PREFIX = "pipeline-";
    private static final String PIPELINE_SERVICE_PREFIX = "pipeline:";
    private static final String RUNNING_STATUS = "running";
    private static final long DEFAULT_SLEEP_MS = 2000L;

    /**
     * 测试方法
     */
    public void test() {
        String pipelineId = IdUtil.objectId();
        List<PipelineNodeVo> pipelineNodes = initialize();
        execute(pipelineId, pipelineNodes);

        ThreadUtil.safeSleep(5000);
        stopForcibly(pipelineId);
        ThreadUtil.safeSleep(1000000);
    }

    /**
     * 执行流水线
     *
     * @param pipelineId    流水线ID
     * @param pipelineNodes 流水线节点列表
     */
    public void execute(String pipelineId, List<PipelineNodeVo> pipelineNodes) {
        if (runningTasks.containsKey(pipelineId)) {
            log.warn("Pipeline [{}] is already running", pipelineId);
            return;
        }

        stopFlags.put(pipelineId, new AtomicBoolean(false));

        Thread pipelineThread = new Thread(() -> processPipeline(pipelineId, pipelineNodes));
        pipelineThread.setDaemon(true);
        pipelineThread.setName(PIPELINE_THREAD_PREFIX + pipelineId);

        runningTasks.put(pipelineId, pipelineThread);
        pipelineThread.start();
    }

    /**
     * 处理流水线任务
     */
    private void processPipeline(String pipelineId, List<PipelineNodeVo> pipelineNodes) {
        try {
            for (PipelineNodeVo node : pipelineNodes) {
                if (shouldStop(pipelineId)) {
                    log.info("Pipeline [{}] was interrupted", pipelineId);
                    return;
                }

                executePipelineNode(pipelineId, node);

                while (!shouldStop(pipelineId) && RUNNING_STATUS.equals(node.getExecuteStatus())) {
                    log.debug("Pipeline [{}] is running...", pipelineId);
                    sleepInterruptibly(DEFAULT_SLEEP_MS, pipelineId);
                }

                if (shouldStop(pipelineId)) {
                    log.info("Pipeline [{}] was interrupted", pipelineId);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("流水线异常", e);
        } finally {
            cleanup(pipelineId);
        }
    }

    /**
     * 检查是否应该停止
     */
    private boolean shouldStop(String pipelineId) {
        return Optional.ofNullable(stopFlags.get(pipelineId))
                .map(AtomicBoolean::get)
                .orElse(true);
    }

    /**
     * 可中断的睡眠
     */
    private void sleepInterruptibly(long millis, String pipelineId) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.info("Pipeline [{}] sleep interrupted", pipelineId);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 清理资源
     */
    private void cleanup(String pipelineId) {
        runningTasks.remove(pipelineId);
        stopFlags.remove(pipelineId);
        log.info("Pipeline [{}] resources cleaned up", pipelineId);
    }

    /**
     * 检查流水线是否在运行
     */
    public boolean isRunning(String pipelineId) {
        return Optional.ofNullable(runningTasks.get(pipelineId))
                .map(Thread::isAlive)
                .orElse(false);
    }

    /**
     * 执行单个流水线节点
     */
    private void executePipelineNode(String pipelineId, PipelineNodeVo node) {
        String serviceName = PIPELINE_SERVICE_PREFIX + node.getBizCode();
        SerialPipelineNodeHandler<Object, Object> handler = SpringBeanHolder.getBean(serviceName);
        try {
            Object result = handler.doProcess(pipelineId, node);
            log.info("Pipeline [{}] node [{}] executed, result: {}", pipelineId, node.getName(), result);
        } catch (BadRequestException e) {
            handlePipelineError(pipelineId, "Business error in pipeline", e);
        } catch (Exception e) {
            handlePipelineError(pipelineId, "Unexpected error in pipeline", e);
        }
    }

    /**
     * 处理流水线错误
     */
    private void handlePipelineError(String pipelineId, String message, Exception e) {
        log.error(message, e);
        if (isRunning(pipelineId)) {
            stopForcibly(pipelineId);
        }
    }

    /**
     * 强制停止流水线
     */
    public void stopForcibly(String pipelineId) {
        Optional.ofNullable(stopFlags.get(pipelineId))
                .ifPresent(flag -> flag.set(true));

        Optional.ofNullable(runningTasks.get(pipelineId))
                .ifPresent(thread -> {
                    try {
                        thread.interrupt();
                    } catch (SecurityException e) {
                        log.error("Failed to interrupt pipeline [{}]", pipelineId, e);
                    }
                });
    }

    /**
     * 初始化测试流水线节点
     */
    public List<PipelineNodeVo> initialize() {
        List<PipelineNodeVo> nodes = new ArrayList<>();

        nodes.add(createNode("初始化", "create_release_branch"));
        nodes.add(createNode("代码构建", "build_code"));

        return nodes;
    }

    /**
     * 创建流水线节点
     */
    private PipelineNodeVo createNode(String name, String bizCode) {
        PipelineNodeVo node = new PipelineNodeVo();
        node.setName(name);
        node.setClick(true);
        node.setButtonList(null);
        node.setBizCode(bizCode);
        node.setStartTimeMillis(DateTime.now().getTime());
        node.setDurationMillis(0L);
        node.setExecuteStatus(RUNNING_STATUS);
        node.setResultDesc(null);
        return node;
    }
}