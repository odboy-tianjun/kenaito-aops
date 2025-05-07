package cn.odboy.exception.handler;

import lombok.Data;


@Data
public final class ApiError {

    private Integer status = 400;
    private Long timestamp;
    private String message;

    private ApiError() {
        timestamp = System.currentTimeMillis();
    }

    public static ApiError error(final String message) {
        ApiError apiError = new ApiError();
        apiError.setMessage(message);
        return apiError;
    }

    public static ApiError error(final Integer status, final String message) {
        ApiError apiError = new ApiError();
        apiError.setStatus(status);
        apiError.setMessage(message);
        return apiError;
    }
}


