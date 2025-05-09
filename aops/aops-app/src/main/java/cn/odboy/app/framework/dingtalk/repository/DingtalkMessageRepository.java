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
import cn.odboy.app.framework.dingtalk.config.DingtalkProperties;
import cn.odboy.app.framework.dingtalk.util.DingtalkClientConfigUtil;
import cn.odboy.app.framework.dingtalk.context.DingtalkApiClientManager;
import cn.odboy.app.framework.dingtalk.exception.DingtalkApiExceptionCatch;
import com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingResponse;
import com.aliyun.dingtalkrobot_1_0.models.RobotSendDingResponse;
import com.aliyun.dingtalkrobot_1_0.models.RobotSendDingResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationGetsendresultRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationRecallRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiMessageCorpconversationGetsendresultResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationRecallResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 消息通知
 *
 * @author odboy
 * @date 2025-01-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DingtalkMessageRepository {
    private final DingtalkProperties dingtalkProperties;
    private final DingtalkApiClientManager dingtalkApiClientManager;

    /**
     * @param taskId 发送工作通知后返回的taskId
     * @return /
     */
    @SneakyThrows
    @DingtalkApiExceptionCatch(description = "查询工作通知的发送结果", throwException = false)
    public OapiMessageCorpconversationGetsendresultResponse.AsyncSendResult describeWorkNoticeSendResult(Long taskId) {
        Assert.notNull(taskId, "taskId不能为空");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/getsendresult");
        OapiMessageCorpconversationGetsendresultRequest req = new OapiMessageCorpconversationGetsendresultRequest();
        req.setAgentId(Long.valueOf(dingtalkProperties.getAgentId()));
        req.setTaskId(taskId);
        OapiMessageCorpconversationGetsendresultResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.getSendResult();
    }

    /**
     * @param dingtalkUserIds 接收人Dingtalk用户Id列表
     * @param content         文本内容
     * @param toAllUser       是否发送给所有人
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "异步发送文本工作通知")
    public Long asyncSendTextWorkNotice(List<String> dingtalkUserIds, String content, boolean toAllUser) throws Exception {
        Assert.notEmpty(dingtalkUserIds, "接收人Dingtalk用户Id列表不能为空");
        Assert.notBlank(content, "通知内容不能为空");
        Assert.checkBetween(dingtalkUserIds.size(), 1, 100, "接收人Dingtalk用户Id列表长度范围1~100");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
        req.setAgentId(Long.valueOf(dingtalkProperties.getAgentId()));
        req.setUseridList(String.join(",", dingtalkUserIds));
        req.setToAllUser(toAllUser);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        OapiMessageCorpconversationAsyncsendV2Request.Text text = new OapiMessageCorpconversationAsyncsendV2Request.Text();
        text.setContent(content);
        msg.setText(text);
        req.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.getTaskId();
    }

    /**
     * @param dingtalkUserIds 接收人Dingtalk用户Id列表
     * @param title           标题
     * @param text            内容
     * @param toAllUser       是否发送给所有人
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "异步发送Markdown工作通知")
    public Long asyncSendMarkdownWorkNotice(List<String> dingtalkUserIds, String title, String text, boolean toAllUser) throws Exception {
        Assert.notEmpty(dingtalkUserIds, "接收人Dingtalk用户Id列表不能为空");
        Assert.notBlank(title, "标题不能为空");
        Assert.notBlank(text, "通知内容不能为空");
        Assert.checkBetween(dingtalkUserIds.size(), 1, 100, "接收人Dingtalk用户Id列表长度范围1~100");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
        req.setAgentId(Long.valueOf(dingtalkProperties.getAgentId()));
        req.setUseridList(String.join(",", dingtalkUserIds));
        req.setToAllUser(toAllUser);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("markdown");
        OapiMessageCorpconversationAsyncsendV2Request.Markdown markdown = new OapiMessageCorpconversationAsyncsendV2Request.Markdown();
        markdown.setText(text);
        markdown.setTitle(title);
        msg.setMarkdown(markdown);
        req.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.getTaskId();
    }


    /**
     * @param taskId 发送工作通知后返回的taskId
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "撤销工作通知")
    public boolean revokeWorkNotice(Long taskId) throws Exception {
        Assert.notNull(taskId, "taskId不能为空");
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/recall");
        OapiMessageCorpconversationRecallRequest req = new OapiMessageCorpconversationRecallRequest();
        req.setAgentId(Long.valueOf(dingtalkProperties.getAgentId()));
        req.setMsgTaskId(taskId);
        OapiMessageCorpconversationRecallResponse rsp = client.execute(req, dingtalkApiClientManager.getClient());
        return rsp.getErrcode() == 0;
    }

    /**
     * @param dingtalkUserIds 接收人Dingtalk用户Id列表
     * @param content         消息内容
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "发送ding消息")
    public RobotSendDingResponseBody sendDingNotice(List<String> dingtalkUserIds, String content) throws Exception {
        Assert.notEmpty(dingtalkUserIds, "接收人Dingtalk用户Id列表不能为空");
        Assert.checkBetween(dingtalkUserIds.size(), 1, 200, "接收人Dingtalk用户Id列表长度范围1~100");
        Assert.notBlank(content, "通知内容不能为空");
        com.aliyun.dingtalkrobot_1_0.Client client = DingtalkClientConfigUtil.createRobotClient();
        com.aliyun.dingtalkrobot_1_0.models.RobotSendDingHeaders robotSendDingHeaders = new com.aliyun.dingtalkrobot_1_0.models.RobotSendDingHeaders();
        robotSendDingHeaders.xAcsDingtalkAccessToken = dingtalkApiClientManager.getClient();
        com.aliyun.dingtalkrobot_1_0.models.RobotSendDingRequest robotSendDingRequest = new com.aliyun.dingtalkrobot_1_0.models.RobotSendDingRequest()
                .setRobotCode(dingtalkProperties.getRobotCode())
                .setContent(content)
                .setRemindType(1)
                .setReceiverUserIdList(dingtalkUserIds);
        RobotSendDingResponse robotSendDingResponse = client.robotSendDingWithOptions(robotSendDingRequest, robotSendDingHeaders, new RuntimeOptions());
        return robotSendDingResponse.getBody();
    }

    /**
     * @param openDingId 发送ding消息后返回的openDingId
     * @return /
     */
    @DingtalkApiExceptionCatch(description = "撤回ding消息")
    public String revokeDingNotice(String openDingId) throws Exception {
        Assert.notBlank(openDingId, "openDingId不能为空");
        com.aliyun.dingtalkrobot_1_0.Client client = DingtalkClientConfigUtil.createRobotClient();
        com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingHeaders robotRecallDingHeaders = new com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingHeaders();
        robotRecallDingHeaders.xAcsDingtalkAccessToken = dingtalkApiClientManager.getClient();
        com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingRequest robotRecallDingRequest = new com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingRequest()
                .setRobotCode(dingtalkProperties.getRobotCode())
                .setOpenDingId(openDingId);
        RobotRecallDingResponse robotRecallDingResponse = client.robotRecallDingWithOptions(robotRecallDingRequest, robotRecallDingHeaders, new RuntimeOptions());
        return robotRecallDingResponse.getBody().getOpenDingId();
    }
}
