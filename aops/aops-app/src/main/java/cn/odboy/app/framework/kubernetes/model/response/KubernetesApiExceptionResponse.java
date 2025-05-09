package cn.odboy.app.framework.kubernetes.model.response;

import cn.odboy.common.pojo.MyObject;
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
