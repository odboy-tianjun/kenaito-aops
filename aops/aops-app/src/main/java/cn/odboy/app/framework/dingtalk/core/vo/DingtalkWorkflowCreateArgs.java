/*
 *  Copyright 2021-2025 Odboy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.app.framework.dingtalk.core.vo;

import cn.odboy.app.framework.dingtalk.core.constant.DingtalkProcessInstanceApproverActionTypeEnum;
import cn.odboy.common.pojo.MyObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * 审批流 创建审批
 *
 * @author odboy
 * @date 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DingtalkWorkflowCreateArgs extends MyObject {
    /**
     * 流程Code
     */
    @NotBlank(message = "流程Code不能为空")
    private String processCode;
    /**
     * 发起人UserId
     */
    @NotBlank(message = "发起人UserId不能为空")
    private String originatorUserId;
    /**
     * 表单内容
     */
    @NotNull(message = "表单内容不能为空")
    @Size(min = 1, message = "表单至少得有一项内容")
    private Map<String, String> formValues;
    /**
     * 审批人列表
     */
    @NotNull(message = "审批人列表不能为空")
    @Size(min = 1, message = "审批人列表至少得有一个节点")
    private List<DingtalkWorkflowApprover> approvers;
    /**
     * 抄送人列表
     */
    @Size(max = 50, message = "抄送人列表最大长度50")
    private List<String> ccList;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class DingtalkWorkflowApprover extends MyObject {
        @NotNull(message = "审批类型不能为空")
        private DingtalkProcessInstanceApproverActionTypeEnum actionType;
        @NotNull(message = "审批人UserId不能为空")
        @Size(min = 1, message = "审批人至少得有一个")
        private List<String> userIds;
    }
}