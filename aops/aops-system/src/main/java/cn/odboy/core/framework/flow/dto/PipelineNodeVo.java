package cn.odboy.core.framework.flow.dto;

import cn.odboy.base.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PipelineNodeVo extends MyObject {
    /**
     * 业务节点名称
     */
    private String name;
    /**
     * 是否可以点击
     */
    private Boolean click = false;
    /**
     * 功能按钮列表，与click互斥
     */
    private List<PipelineNodeOperationButton> buttonList;
    /**
     * 业务编码(createReleaseBranch、mergeCode)
     */
    private String bizCode;
    /**
     * 业务节点开始时间
     */
    private Long startTimeMillis;
    /**
     * 业务节点耗时
     */
    private Long durationMillis;
    /**
     * 业务节点执行状态(success 成功, error 失败, running 运行中)
     */
    private String executeStatus;
    /**
     * 业务节点结果描述(执行成功)
     */
    private String resultDesc;

    /**
     * 流水线节点功能按钮
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class PipelineNodeOperationButton extends MyObject {
        /**
         * 请求方式(get、post)
         */
        private String method;
        /**
         * 请求路径(/api/doCheck?id=1&bizCode=)
         */
        private String requestUrl;
        /**
         * 按钮名称(通过、打回)
         */
        private String text;
        /**
         * 按钮类型(execute 请求url、link 跳转三方)
         */
        private String type;
    }
}
