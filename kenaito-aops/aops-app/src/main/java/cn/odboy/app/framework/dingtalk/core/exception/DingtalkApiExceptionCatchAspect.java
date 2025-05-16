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
package cn.odboy.app.framework.dingtalk.core.exception;

import cn.hutool.core.date.TimeInterval;
import cn.odboy.app.util.LogFmtUtil;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.util.ReturnValueHandleUtil;
import com.aliyun.tea.TeaException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * DingtalkApi调用异常
 *
 * @author odboy
 * @date 2025-05-07
 */
@Slf4j
@Aspect
@Component
public class DingtalkApiExceptionCatchAspect {
    @Around("@annotation(dingtalkApiExceptionCatch)")
    public Object aroundDingtalkApiExceptionCatch(ProceedingJoinPoint joinPoint, DingtalkApiExceptionCatch dingtalkApiExceptionCatch) throws Throwable {
        return handleDingtalkApiException(joinPoint, dingtalkApiExceptionCatch);
    }

    private Object handleDingtalkApiException(ProceedingJoinPoint joinPoint, DingtalkApiExceptionCatch annotation) {
        TimeInterval timeInterval = new TimeInterval();
        try {
            Object result = joinPoint.proceed();
            log.info("接口 [{}] 执行耗时: {} ms", annotation.description(), timeInterval.intervalMs());
            return result;
        } catch (TeaException teaException) {
            if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
                String exceptionMessage = annotation.description() + "失败, code={}, message={}";
                log.error(exceptionMessage, teaException.code, teaException.message, teaException);
                if (annotation.throwException()) {
                    throw new BadRequestException(cn.odboy.app.util.LogFmtUtil.format(exceptionMessage, teaException.code, teaException.message));
                }
            }
            String exceptionMessage = annotation.description() + "失败";
            log.error(exceptionMessage, teaException);
            if (annotation.throwException()) {
                throw new BadRequestException(exceptionMessage);
            }
        } catch (Throwable exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                String exceptionMessage = annotation.description() + "失败, code={}, message={}";
                log.error(exceptionMessage, err.code, err.message, err);
                if (annotation.throwException()) {
                    throw new BadRequestException(LogFmtUtil.format(exceptionMessage, err.code, err.message));
                }
            }
            String exceptionMessage = annotation.description() + "失败";
            log.error(exceptionMessage, exception);
            if (annotation.throwException()) {
                throw new BadRequestException(exceptionMessage);
            }
        }
        return ReturnValueHandleUtil.getDefaultValue(joinPoint);
    }
}
