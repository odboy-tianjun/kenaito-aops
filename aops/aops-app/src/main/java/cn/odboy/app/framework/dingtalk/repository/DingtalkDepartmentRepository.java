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
import cn.odboy.app.framework.dingtalk.context.DingtalkApiClientManager;
import cn.odboy.app.framework.dingtalk.exception.DingtalkApiExceptionCatch;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2DepartmentListsubRequest;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Consumer;

/**
 * 部门管理 2.0
 *
 * @author odboy
 * @date 2025-01-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DingtalkDepartmentRepository {
    private final DingtalkApiClientManager dingtalkApiClientManager;

    /**
     * @param deptId 部门Id
     * @return /
     */
    @SneakyThrows
    @DingtalkApiExceptionCatch(description = "查询部门列表", throwException = false)
    public List<OapiV2DepartmentListsubResponse.DeptBaseResponse> describeSubDepartmentListByDeptId(Long deptId) {
        Assert.notNull(deptId, "部门Id不能为空");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsub");
        OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
        req.setDeptId(deptId);
        req.setLanguage("zh_CN");
        OapiV2DepartmentListsubResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.getResult();
    }


    private void loopConsumerDepartmentList(long deptId, Consumer<List<OapiV2DepartmentListsubResponse.DeptBaseResponse>> consumer) {
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptBaseResponses = describeSubDepartmentListByDeptId(deptId);
        if (deptBaseResponses.isEmpty()) {
            return;
        }
        consumer.accept(deptBaseResponses);
        for (OapiV2DepartmentListsubResponse.DeptBaseResponse deptBaseResponse : deptBaseResponses) {
            loopConsumerDepartmentList(deptBaseResponse.getDeptId(), consumer);
        }
    }
}
