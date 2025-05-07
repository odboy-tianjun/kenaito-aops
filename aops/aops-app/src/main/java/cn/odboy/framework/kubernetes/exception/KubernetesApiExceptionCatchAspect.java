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
package cn.odboy.framework.kubernetes.exception;

import cn.hutool.core.date.TimeInterval;
import cn.odboy.exception.BadRequestException;
import cn.odboy.framework.kubernetes.model.response.KubernetesApiExceptionResponse;
import com.alibaba.fastjson2.JSON;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * KubernetesApi调用异常
 *
 * @author odboy
 * @date 2025-05-07
 */
@Slf4j
@Aspect
@Component
public class KubernetesApiExceptionCatchAspect {
    @Around("@annotation(kubernetesApiExceptionCatch)")
    public Object aroundKubernetesApiExceptionCatch(ProceedingJoinPoint joinPoint, KubernetesApiExceptionCatch kubernetesApiExceptionCatch) throws Throwable {
        return handleKubernetesApiException(joinPoint, kubernetesApiExceptionCatch);
    }

    private Object handleKubernetesApiException(ProceedingJoinPoint joinPoint, KubernetesApiExceptionCatch annotation) {
        var timeInterval = new TimeInterval();
        try {
            Object result = joinPoint.proceed();
            log.info("接口 [{}] 执行耗时: {} ms", annotation.description(), timeInterval.intervalMs());
            return result;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("接口 [{}] 执行异常，耗时: {} ms，异常信息: {}", annotation.description(), timeInterval.intervalMs(), responseBody, e);
            if (annotation.throwException()) {
                KubernetesApiExceptionResponse actionExceptionBody = JSON.parseObject(responseBody, KubernetesApiExceptionResponse.class);
                if (actionExceptionBody != null) {
                    throw new BadRequestException(annotation.description() + "失败, 原因：" + actionExceptionBody.getReason());
                }
                throw new BadRequestException(annotation.description() + "失败");
            }
        } catch (Throwable e) {
            log.error("接口 [{}] 执行异常，耗时: {} ms", annotation.description(), timeInterval.intervalMs(), e);
            if (annotation.throwException()) {
                throw new BadRequestException(annotation.description() + "失败");
            }
        }
        // 记得判空，虽然这会留下坑
        return null;
    }
}
