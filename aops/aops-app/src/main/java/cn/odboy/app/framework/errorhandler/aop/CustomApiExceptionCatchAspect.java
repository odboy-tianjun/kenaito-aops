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
package cn.odboy.app.framework.errorhandler.aop;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.odboy.app.framework.errorhandler.annotaions.CustomApiExceptionCatch;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.util.ReturnValueHandleUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * CustomApi调用异常
 *
 * @author odboy
 * @date 2025-05-07
 */
@Slf4j
@Aspect
@Component
public class CustomApiExceptionCatchAspect {
    @Around("@annotation(customApiExceptionCatch)")
    public Object aroundApiExceptionCatch(ProceedingJoinPoint joinPoint, CustomApiExceptionCatch customApiExceptionCatch) throws Throwable {
        return handleApiException(joinPoint, customApiExceptionCatch);
    }

    private Object handleApiException(ProceedingJoinPoint joinPoint, CustomApiExceptionCatch annotation) {
        TimeInterval timeInterval = new TimeInterval();
        try {
            Object result = joinPoint.proceed();
            log.info("接口 [{}] 执行耗时: {} ms", annotation.description(), timeInterval.intervalMs());
            return result;
        } catch (Throwable exception) {
            String exceptionMessage = annotation.description() + "失败";
            if (StrUtil.isNotBlank(exception.getMessage())) {
                exceptionMessage = exceptionMessage + "，原因：" + exception.getMessage();
            }
            log.error(exceptionMessage, exception);
            if (annotation.throwException()) {
                throw new BadRequestException(exceptionMessage);
            }
        }
        return ReturnValueHandleUtil.getDefaultValue(joinPoint);
    }
}
