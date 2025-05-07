package cn.odboy.util;

import cn.odboy.core.framework.flow.context.PipelineManager;
import cn.odboy.core.framework.flow.context.PipelineProcessManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PipelineTests {
    @Autowired
    private PipelineManager pipelineManager;
    @Autowired
    private PipelineProcessManager pipelineProcessManager;

    @Test
    public void testPipelineManager() {
        pipelineManager.test();
    }

    @Test
    public void testPipelineProcessManager() {
        pipelineProcessManager.test();
    }
}

