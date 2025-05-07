package cn.odboy.websocket.dto;

import cn.odboy.websocket.constant.WebSocketMessageBizTypeEnum;
import lombok.Data;

@Data
public class WebSocketMessageDto {
    private String message;
    private WebSocketMessageBizTypeEnum bizType;

    public WebSocketMessageDto(String message, WebSocketMessageBizTypeEnum bizType) {
        this.message = message;
        this.bizType = bizType;
    }
}
