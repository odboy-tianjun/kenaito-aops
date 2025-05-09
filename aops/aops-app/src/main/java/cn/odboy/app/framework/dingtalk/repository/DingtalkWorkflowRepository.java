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
package cn.odboy.app.framework.dingtalk.repository;

import cn.hutool.core.lang.Assert;
import cn.odboy.app.framework.dingtalk.constant.DingtalkProcessInstanceResultEnum;
import cn.odboy.app.framework.dingtalk.util.DingtalkClientConfigUtil;
import cn.odboy.app.framework.dingtalk.context.DingtalkApiClientManager;
import cn.odboy.app.framework.dingtalk.exception.DingtalkApiExceptionCatch;
import cn.odboy.app.framework.dingtalk.model.DingtalkWorkflowCreateArgs;
import cn.odboy.common.util.ValidationUtil;
import com.aliyun.dingtalkworkflow_1_0.models.ExecuteProcessInstanceResponse;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponse;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponseBody;
import com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceResponse;
import com.aliyun.dingtalkworkflow_1_0.models.TerminateProcessInstanceResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

/**
 * 审批流
 *
 * @author odboy
 * @date 2025-01-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DingtalkWorkflowRepository {
    private final DingtalkApiClientManager dingtalkApiClientManager;

    @DingtalkApiExceptionCatch(description = "创建审批流")
    public String createWorkflow(DingtalkWorkflowCreateArgs args) throws Exception {
        ValidationUtil.validate(args);
        com.aliyun.dingtalkworkflow_1_0.Client client = DingtalkClientConfigUtil.createWorkflowClient();
        com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceHeaders startProcessInstanceHeaders = new com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceHeaders();
        startProcessInstanceHeaders.xAcsDingtalkAccessToken = dingtalkApiClientManager.getClient();
        com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest startProcessInstanceRequest = new com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest()
                .setApprovers(args.getApprovers()
                        .stream()
                        .map(m -> new com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest.StartProcessInstanceRequestApprovers()
                                .setActionType(m.getActionType().getCode())
                                .setUserIds(m.getUserIds()))
                        .collect(Collectors.toList())
                )
                .setCcList(args.getCcList())
                .setCcPosition("FINISH")
                .setOriginatorUserId(args.getOriginatorUserId())
                .setProcessCode(args.getProcessCode())
                .setFormComponentValues(args.getFormValues()
                        .entrySet()
                        .stream()
                        .map(m -> new com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValues()
                                .setName(m.getKey())
                                .setValue(m.getValue())
                        )
                        .collect(Collectors.toList()));
        StartProcessInstanceResponse startProcessInstanceResponse = client.startProcessInstanceWithOptions(startProcessInstanceRequest, startProcessInstanceHeaders, new RuntimeOptions());
        return startProcessInstanceResponse.getBody().getInstanceId();
    }

    /**
     * @param processInstanceId 审批流实例Id
     * @return /
     */
    @SneakyThrows
    @DingtalkApiExceptionCatch(description = "获取审批流实例详情", throwException = false)
    public GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult describeWorkflowByInstanceId(String processInstanceId) {
        Assert.notBlank(processInstanceId, "审批流实例Id不能为空");
        com.aliyun.dingtalkworkflow_1_0.Client client = DingtalkClientConfigUtil.createWorkflowClient();
        com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceHeaders getProcessInstanceHeaders = new com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceHeaders();
        getProcessInstanceHeaders.xAcsDingtalkAccessToken = dingtalkApiClientManager.getClient();
        com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceRequest getProcessInstanceRequest = new com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceRequest()
                .setProcessInstanceId(processInstanceId);
        GetProcessInstanceResponse getProcessInstanceResponse = client.getProcessInstanceWithOptions(getProcessInstanceRequest, getProcessInstanceHeaders, new RuntimeOptions());
        return getProcessInstanceResponse.getBody().getResult();
    }

    /**
     * @param processInstanceId     审批流实例Id
     * @param actionUserId          操作人UserId
     * @param taskId                审批流实例任务Id
     * @param processInstanceResult 审批操作
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "改变审批流节点审批状态")
    public Boolean modifyWorkflowTaskResult(String processInstanceId, String actionUserId, Long taskId, DingtalkProcessInstanceResultEnum processInstanceResult) throws Exception {
        Assert.notBlank(processInstanceId, "审批流实例Id不能为空");
        Assert.notBlank(actionUserId, "操作人UserId不能为空");
        Assert.notNull(taskId, "审批流实例任务Id不能为空");
        Assert.notNull(processInstanceResult, "审批操作不能为空");
        com.aliyun.dingtalkworkflow_1_0.Client client = DingtalkClientConfigUtil.createWorkflowClient();
        com.aliyun.dingtalkworkflow_1_0.models.ExecuteProcessInstanceHeaders executeProcessInstanceHeaders = new com.aliyun.dingtalkworkflow_1_0.models.ExecuteProcessInstanceHeaders();
        executeProcessInstanceHeaders.xAcsDingtalkAccessToken = dingtalkApiClientManager.getClient();
        com.aliyun.dingtalkworkflow_1_0.models.ExecuteProcessInstanceRequest executeProcessInstanceRequest = new com.aliyun.dingtalkworkflow_1_0.models.ExecuteProcessInstanceRequest()
                .setResult(processInstanceResult.getCode())
                .setProcessInstanceId(processInstanceId)
                .setActionerUserId(actionUserId)
                .setTaskId(taskId);
        ExecuteProcessInstanceResponse executeProcessInstanceResponse = client.executeProcessInstanceWithOptions(executeProcessInstanceRequest, executeProcessInstanceHeaders, new RuntimeOptions());
        return executeProcessInstanceResponse.getBody().getResult();
    }

    /**
     * @param processInstanceId 审批流实例Id
     * @param remark            终止说明
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "撤销审批流实例")
    public Boolean revokeWorkflow(String processInstanceId, String remark) throws Exception {
        Assert.notBlank(processInstanceId, "审批流实例Id不能为空");
        com.aliyun.dingtalkworkflow_1_0.Client client = DingtalkClientConfigUtil.createWorkflowClient();
        com.aliyun.dingtalkworkflow_1_0.models.TerminateProcessInstanceHeaders terminateProcessInstanceHeaders = new com.aliyun.dingtalkworkflow_1_0.models.TerminateProcessInstanceHeaders();
        terminateProcessInstanceHeaders.xAcsDingtalkAccessToken = dingtalkApiClientManager.getClient();
        com.aliyun.dingtalkworkflow_1_0.models.TerminateProcessInstanceRequest terminateProcessInstanceRequest = new com.aliyun.dingtalkworkflow_1_0.models.TerminateProcessInstanceRequest()
                .setIsSystem(true)
                .setProcessInstanceId(processInstanceId)
                .setRemark(remark);
        TerminateProcessInstanceResponse terminateProcessInstanceResponse = client.terminateProcessInstanceWithOptions(terminateProcessInstanceRequest, terminateProcessInstanceHeaders, new RuntimeOptions());
        return terminateProcessInstanceResponse.getBody().getResult();
    }
}
