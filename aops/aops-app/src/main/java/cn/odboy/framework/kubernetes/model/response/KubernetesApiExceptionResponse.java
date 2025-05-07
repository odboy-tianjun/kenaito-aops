package cn.odboy.framework.kubernetes.model.response;

import cn.odboy.base.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KubernetesApiExceptionResponse extends MyObject {
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
