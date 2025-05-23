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
package cn.odboy.app.framework.dingtalk.core.repository;

import cn.hutool.core.lang.Assert;
import cn.odboy.app.framework.dingtalk.core.context.DingtalkApiClientManager;
import cn.odboy.app.framework.dingtalk.core.exception.DingtalkApiExceptionCatch;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserCountRequest;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiUserCountResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Consumer;

/**
 * 用户管理 2.0
 *
 * @author odboy
 * @date 2025-01-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DingtalkUserRepository {
    private final DingtalkApiClientManager dingtalkApiClientManager;

    /**
     * @param deptId 部门Id
     * @param cursor 游标，从0开始
     * @return /
     */
    @SneakyThrows
    @DingtalkApiExceptionCatch(description = "分页查询部门用户", throwException = false)
    private OapiV2UserListResponse.PageResult describeDepartmentUserPage(Long deptId, Long cursor) {
        Assert.notNull(deptId, "部门Id不能为空");
        Assert.notNull(cursor, "游标不能为空");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
        OapiV2UserListRequest req = new OapiV2UserListRequest();
        req.setDeptId(deptId);
        req.setCursor(cursor);
        req.setSize(100L);
        req.setLanguage("zh_CN");
        OapiV2UserListResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.getResult();
    }

    /**
     * 获取部门用户列表
     *
     * @param deptId   部门Id
     * @param consumer /
     */
    public void loopConsumerDepartmentUserList(Long deptId, Consumer<List<OapiV2UserListResponse.ListUserResponse>> consumer) {
        Long cursor = 0L;
        while (true) {
            OapiV2UserListResponse.PageResult pageResult = describeDepartmentUserPage(deptId, cursor);
            if (pageResult == null) {
                break;
            }
            List<OapiV2UserListResponse.ListUserResponse> list = pageResult.getList();
            if (list.isEmpty()) {
                break;
            }
            consumer.accept(list);
            cursor = pageResult.getNextCursor();
        }
    }

    /**
     * @param deptId 部门Id
     * @return /
     */
    @SneakyThrows
    @DingtalkApiExceptionCatch(description = "查询部门下用户数量", throwException = false)
    public Long getDepartmentUserCount(Long deptId) {
        Assert.notNull(deptId, "部门Id不能为空");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/count");
        OapiUserCountRequest req = new OapiUserCountRequest();
        req.setOnlyActive(true);
        OapiUserCountResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.getResult().getCount();
    }
}
