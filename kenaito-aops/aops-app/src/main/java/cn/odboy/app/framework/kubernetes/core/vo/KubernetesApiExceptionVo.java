package cn.odboy.app.framework.kubernetes.core.vo;

import cn.odboy.common.model.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KubernetesApiExceptionVo extends MyObject {
    private String kind;
    private String apiVersion;
    private String status;
    private String message;
    private String reason;
    private Details details;
    private int code;

    @Data
    public static class Details {
        private String name;
        private String kind;
    }
}
