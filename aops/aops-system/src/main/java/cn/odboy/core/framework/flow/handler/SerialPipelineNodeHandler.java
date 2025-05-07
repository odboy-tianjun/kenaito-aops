package cn.odboy.core.framework.flow.handler;

import cn.hutool.core.thread.ThreadUtil;
import cn.odboy.exception.BadRequestException;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * 串行流水线节点处理器
 *
 * @author odboy
 */
@Slf4j
public abstract class SerialPipelineNodeHandler<I, T> {

    /**
     * 预处理
     *
     * @param pipelineId 流水线id
     * @param inputModel 输入参数
     */
    protected abstract void preProcess(String pipelineId, I inputModel);

    /**
     * 预处理异常
     *
     * @param pipelineId 流水线id
     * @param inputModel 输入参数
     * @param e          异常信息
     */
    protected abstract void preProcessException(String pipelineId, I inputModel, Exception e);

    /**
     * 处理业务
     *
     * @param pipelineId 流水线id
     * @param inputModel 输入参数
     */
    protected abstract T process(String pipelineId, I inputModel);

    /**
     * 处理业务异常
     *
     * @param pipelineId 流水线id
     * @param inputModel 输入参数
     * @param e          异常信息
     */
    protected abstract void processException(String pipelineId, I inputModel, Exception e);

    /**
     * 业务后置处理
     *
     * @param pipelineId 流水线id
     * @param inputModel 输入参数
     */
    protected abstract void postProcess(String pipelineId, I inputModel, T processResult);

    /**
     * 业务后置处理异常
     *
     * @param pipelineId 流水线id
     * @param inputModel 输入参数
     */
    protected abstract void postProcessException(String pipelineId, I inputModel, T processResult, Exception e);

    /**
     * 当前节点是否被锁住（卡点功能），返回false解锁
     *
     * @param pipelineId 流水线id
     * @param inputModel 输入参数
     */
    protected abstract boolean isLocked(String pipelineId, I inputModel);

    /**
     * 当进程被中断时
     *
     * @param pipelineId 流水线id
     * @param inputModel 输入参数
     */
    protected abstract void onProcessInterrupted(String pipelineId, I inputModel);

    /**
     * 业务处理
     *
     * @param pipelineId 流水线id
     * @param inputModel 输入参数
     * @return T
     */
    public T doProcess(String pipelineId, I inputModel) throws BadRequestException {
        try {
            if (Thread.currentThread().isInterrupted()) {
                onProcessInterrupted(pipelineId, inputModel);
                throw new InterruptedException("业务被中断");
            }
            preProcess(pipelineId, inputModel);
            log.info("preProcess,pipelineId={},inputModel={}", pipelineId, JSON.toJSONString(inputModel));
        } catch (Exception e) {
            preProcessException(pipelineId, inputModel, e);
            log.error("preProcessException,pipelineId={},inputModel={}", pipelineId, JSON.toJSONString(inputModel), e);
            throw new BadRequestException(e.getMessage());
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new BadRequestException("业务被中断");
        }
        while (isLocked(pipelineId, inputModel)) {
            ThreadUtil.safeSleep(2000);
        }
        try {
            if (Thread.currentThread().isInterrupted()) {
                throw new BadRequestException("业务被中断");
            }
            T processResult = process(pipelineId, inputModel);
            log.info("process,pipelineId={},inputModel={}", pipelineId, JSON.toJSONString(inputModel));
            try {
                postProcess(pipelineId, inputModel, processResult);
                log.info("postProcess,pipelineId={},inputModel={},processResult={}", pipelineId, JSON.toJSONString(inputModel), JSON.toJSONString(processResult));
                return processResult;
            } catch (Exception e) {
                postProcessException(pipelineId, inputModel, processResult, e);
                log.error("postProcessException,pipelineId={},inputModel={},processResult={}", pipelineId, JSON.toJSONString(inputModel), JSON.toJSONString(processResult));
                throw new BadRequestException(e.getMessage());
            }
        } catch (Exception e) {
            processException(pipelineId, inputModel, e);
            log.error("processException,pipelineId={},inputModel={}", pipelineId, JSON.toJSONString(inputModel), e);
            throw new BadRequestException(e.getMessage());
        }
    }
}
