package cn.odboy.app.controller.vo;

import cn.odboy.app.dal.dataobject.AopsProductLineDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AopsProductLineVo extends AopsProductLineDO {
    /**
     * 是否绑定应用
     */
    private Boolean hasApp = false;
    /**
     * 产品线名称
     */
    private String linePathName;
}
