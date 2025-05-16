package cn.odboy.core.framework.flow.core.handler.impl;

import cn.odboy.core.framework.flow.core.handler.SerialPipelineNodeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("pipeline:create_release_branch")
public class PipelineAppCreateReleaseBranchHandlerServiceImpl extends SerialPipelineNodeHandler<Object, Object> {
    @Override
    public void preProcess(String pipelineId, Object inputModel) {
    }

    @Override
    public void preProcessException(String pipelineId, Object inputModel, Exception e) {
    }

    @Override
    public Object process(String pipelineId, Object inputModel) {
        return "Hello World";
    }

    @Override
    public void processException(String pipelineId, Object inputModel, Exception e) {
    }

    @Override
    public void postProcess(String pipelineId, Object inputModel, Object processResult) {
    }

    @Override
    public void postProcessException(String pipelineId, Object inputModel, Object processResult, Exception e) {
    }

    @Override
    public boolean isLocked(String pipelineId, Object inputModel) {
        return false;
    }

    @Override
    protected void onProcessInterrupted(String pipelineId, Object inputModel) {
        log.error("pipeline is stopping");
    }
}
