package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileTypeEnum {
    /* 文件类型 */
    IMAGE("image", "图片"),
    DOC("doc", "文档"),
    VOICE("voice", "音频"),
    VIDEO("video", "视频"),
    OTHER("other", "其他");

    private final String code;
    private final String description;
}
