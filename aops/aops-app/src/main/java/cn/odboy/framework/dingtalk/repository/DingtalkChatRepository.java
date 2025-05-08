/*
 *  Copyright 2021-2025 Tian Jun
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
package cn.odboy.framework.dingtalk.repository;

import cn.hutool.core.lang.Assert;
import cn.odboy.framework.dingtalk.context.DingtalkApiClientManager;
import cn.odboy.framework.dingtalk.exception.DingtalkApiExceptionCatch;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiChatCreateRequest;
import com.dingtalk.api.request.OapiChatGetRequest;
import com.dingtalk.api.request.OapiChatSubadminUpdateRequest;
import com.dingtalk.api.request.OapiChatUpdateRequest;
import com.dingtalk.api.response.OapiChatCreateResponse;
import com.dingtalk.api.response.OapiChatGetResponse;
import com.dingtalk.api.response.OapiChatSubadminUpdateResponse;
import com.dingtalk.api.response.OapiChatUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 群管理
 *
 * @author odboy
 * @date 2025-01-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DingtalkChatRepository {
    private final DingtalkApiClientManager dingtalkApiClientManager;

    /**
     * @param chatId 群Id
     * @return /
     */
    @SneakyThrows
    @DingtalkApiExceptionCatch(description = "获取群信息", throwException = false)
    public OapiChatGetResponse.ChatInfo describeChatById(String chatId) {
        Assert.notBlank(chatId, "群Id不能为空");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/get");
        OapiChatGetRequest req = new OapiChatGetRequest();
        req.setChatid(chatId);
        req.setHttpMethod("GET");
        OapiChatGetResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.getChatInfo();
    }

    /**
     * @param name        群名称
     * @param ownerUserId 群主UserId
     * @param joinUserIds 群成员UserId列表
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "创建群")
    public OapiChatCreateResponse createChat(String name, String ownerUserId, List<String> joinUserIds) throws Exception {
        Assert.notBlank(name, "群名称不能为空");
        Assert.notBlank(ownerUserId, "群主UserId不能为空");
        Assert.notEmpty(joinUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(joinUserIds.size(), 2, 40, "群成员UserId列表长度范围2~40");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/create");
        OapiChatCreateRequest req = new OapiChatCreateRequest();
        req.setName(name);
        req.setOwner(ownerUserId);
        req.setUseridlist(joinUserIds);
        req.setShowHistoryType(1L);
        req.setSearchable(1L);
        req.setValidationType(1L);
        req.setMentionAllAuthority(1L);
        req.setManagementType(1L);
        req.setChatBannedType(0L);
        return client.execute(req, dingtalkApiClientManager.getClient());
    }

    /**
     * @param chatId      群Id
     * @param joinUserIds 群成员UserId列表
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "群加人")
    public boolean joinUsersToChat(String chatId, List<String> joinUserIds) throws Exception {
        Assert.notBlank(chatId, "群Id不能为空");
        Assert.notEmpty(joinUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(joinUserIds.size(), 1, 40, "群成员UserId列表长度范围1~40");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/update");
        OapiChatUpdateRequest req = new OapiChatUpdateRequest();
        req.setChatid(chatId);
        req.setAddUseridlist(joinUserIds);
        OapiChatUpdateResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.isSuccess();
    }

    /**
     * @param chatId         群Id
     * @param kickOutUserIds 群成员UserId列表
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "群踢人")
    public boolean kickOutUsersFromChat(String chatId, List<String> kickOutUserIds) throws Exception {
        Assert.notBlank(chatId, "群Id不能为空");
        Assert.notEmpty(kickOutUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(kickOutUserIds.size(), 1, 40, "群成员UserId列表长度范围1~40");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/update");
        OapiChatUpdateRequest req = new OapiChatUpdateRequest();
        req.setChatid(chatId);
        req.setDelUseridlist(kickOutUserIds);
        OapiChatUpdateResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.isSuccess();
    }

    /**
     * @param chatId 群Id
     * @param name   群名称
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "修改群名称")
    public boolean modifyChatName(String chatId, String name) throws Exception {
        Assert.notBlank(chatId, "群Id不能为空");
        Assert.notBlank(name, "群名称不能为空");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/update");
        OapiChatUpdateRequest req = new OapiChatUpdateRequest();
        req.setChatid(chatId);
        req.setName(name);
        OapiChatUpdateResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.isSuccess();
    }

    /**
     * @param chatId  群Id
     * @param ownerId 群主Id
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "转让群主")
    public boolean transferChatOwner(String chatId, String ownerId) throws Exception {
        Assert.notBlank(chatId, "群Id不能为空");
        Assert.notBlank(ownerId, "群主Id不能为空");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/update");
        OapiChatUpdateRequest req = new OapiChatUpdateRequest();
        req.setChatid(chatId);
        req.setOwner(ownerId);
        OapiChatUpdateResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.isSuccess();
    }

    /**
     * @param chatId      群Id
     * @param joinUserIds 群成员UserId列表
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "添加群管理员")
    public boolean joinAdminUsersToChat(String chatId, List<String> joinUserIds) throws Exception {
        Assert.notBlank(chatId, "群Id不能为空");
        Assert.notEmpty(joinUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(joinUserIds.size(), 1, 40, "群成员UserId列表长度范围1~40");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/chat/subadmin/update");
        OapiChatSubadminUpdateRequest req = new OapiChatSubadminUpdateRequest();
        req.setChatid(chatId);
        req.setUserids(String.join(",", joinUserIds));
        req.setRole(2L);
        OapiChatSubadminUpdateResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.isSuccess();
    }

    /**
     * @param chatId      群Id
     * @param joinUserIds 群成员UserId列表
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "踢出群管理员")
    public boolean kickOutAdminUsersToChat(String chatId, List<String> joinUserIds) throws Exception {
        Assert.notBlank(chatId, "群Id不能为空");
        Assert.notEmpty(joinUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(joinUserIds.size(), 1, 40, "群成员UserId列表长度范围1~40");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/chat/subadmin/update");
        OapiChatSubadminUpdateRequest req = new OapiChatSubadminUpdateRequest();
        req.setChatid(chatId);
        req.setUserids(String.join(",", joinUserIds));
        req.setRole(3L);
        OapiChatSubadminUpdateResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.isSuccess();
    }
}
