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
package cn.odboy.app.framework.gitlab.core.context;

import cn.hutool.core.io.IoUtil;
import cn.odboy.app.constant.AppLanguageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gitlab Ignore文件管理工具
 *
 * @author odboy
 * @date 2024-10-01
 */
@Slf4j
@Component
public class GitlabIgnoreFileAdmin implements InitializingBean {
    private final Map<String, String> innerMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            innerMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("gitlab/ignorefile/java.ignore").getInputStream()));
            innerMap.put(AppLanguageEnum.PYTHON.getCode(), IoUtil.readUtf8(new ClassPathResource("gitlab/ignorefile/python.ignore").getInputStream()));
            innerMap.put(AppLanguageEnum.VUE.getCode(), IoUtil.readUtf8(new ClassPathResource("gitlab/ignorefile/vuejs.ignore").getInputStream()));
            innerMap.put(AppLanguageEnum.GO.getCode(), IoUtil.readUtf8(new ClassPathResource("gitlab/ignorefile/go.ignore").getInputStream()));
            log.info("初始化Gitlab Ignore文件成功");
        } catch (IOException e) {
            log.error("初始化Gitlab Ignore文件失败", e);
        }
    }

    public String getContent(String language) {
        return innerMap.getOrDefault(language, "");
    }
}
