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
package cn.odboy.framework.kubernetes.model.vo;

import cn.hutool.core.lang.Assert;
import cn.odboy.exception.BadRequestException;
import lombok.Value;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Value
public class ArgsAppNameVo {
    private static final Pattern PATTERN = Pattern.compile("^[a-z](?:-?[a-z0-9])*$");

    String value;

    public ArgsAppNameVo(String value) {
        Assert.notBlank(value, "应用名称不能为空");
        if (!PATTERN.matcher(value).matches()) {
            throw new BadRequestException("应用名称命名规则异常，应包含小写字母、符号-和整数，不能以符号-或者整数开头，中间不能有连续符号-");
        }
        this.value = value;
    }

    public static void main(String[] args) {
        String[] testCases = {
                "valid-name",
                "name123",
                "a1b2c3",
                "test-123",
                "test-123-name",
                "single",
                "a",
                "123-invalid",
                "invalid-123",
                "-invalid",
                "invalid-",
                "Invalid",
                "name@test",
                "name--test",
                "test-123-",
                "test-123-456",
                "test--123",
                ""
        };

        for (String test : testCases) {
            Matcher matcher = PATTERN.matcher(test);
            System.out.printf("%-15s => %s%n", test, matcher.matches());
        }
    }
}
