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
package cn.odboy.app.framework.dingtalk.core.context;

import cn.hutool.core.util.StrUtil;
import cn.odboy.app.framework.dingtalk.config.DingtalkProperties;
import cn.odboy.app.framework.dingtalk.core.util.DingtalkClientConfigUtil;
import cn.odboy.app.util.LogFmtUtil;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.redis.RedisHelper;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.tea.TeaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * dingtalk 客户端认证
 *
 * @author odboy
 * @date 2025-01-13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DingtalkApiClientManager {
    private static final String KEY_ACCESS_TOKEN = "dingtalk:accessToken";
    private final DingtalkProperties dingtalkProperties;
    private final RedisHelper redisHelper;

    public String getClient() throws BadRequestException {
        try {
            String accessToken = redisHelper.get(KEY_ACCESS_TOKEN, String.class);
            if (StrUtil.isNotEmpty(accessToken)) {
                return accessToken;
            }
            com.aliyun.dingtalkoauth2_1_0.Client client = DingtalkClientConfigUtil.createAuthClient();
            com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
                    .setAppKey(dingtalkProperties.getAppKey())
                    .setAppSecret(dingtalkProperties.getAppSecret());
            GetAccessTokenResponse accessTokenResponse = client.getAccessToken(getAccessTokenRequest);
            accessToken = accessTokenResponse.body.getAccessToken();
            redisHelper.set(KEY_ACCESS_TOKEN, accessToken, 7000, TimeUnit.SECONDS);
            return accessToken;
        } catch (TeaException teaException) {
            if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
                String exceptionMessage = "获取DingtalkAccessToken失败, code={}, message={}";
                log.error(exceptionMessage, teaException.code, teaException.message, teaException);
                throw new BadRequestException(cn.odboy.app.util.LogFmtUtil.format(exceptionMessage, teaException.code, teaException.message));
            }
            String exceptionMessage = "获取DingtalkAccessToken失败";
            log.error(exceptionMessage, teaException);
            throw new BadRequestException(exceptionMessage);
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                String exceptionMessage = "获取DingtalkAccessToken失败, code={}, message={}";
                log.error(exceptionMessage, err.code, err.message, err);
                throw new BadRequestException(LogFmtUtil.format(exceptionMessage, err.code, err.message));
            }
            String exceptionMessage = "获取DingtalkAccessToken失败";
            log.error(exceptionMessage, exception);
            throw new BadRequestException(exceptionMessage);
        }
    }
}
