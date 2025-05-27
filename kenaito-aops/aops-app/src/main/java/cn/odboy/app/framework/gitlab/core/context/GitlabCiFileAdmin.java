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
import cn.odboy.common.constant.GlobalEnvEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gitlab CI文件管理工具
 *
 * @author odboy
 * @date 2024-10-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabCiFileAdmin implements InitializingBean {
    private final Map<String, String> innerCiFileMap = new HashMap<>();
    private final Map<String, String> innerDockerDailyFileMap = new HashMap<>();
    private final Map<String, String> innerDockerStageFileMap = new HashMap<>();
    private final Map<String, String> innerDockerOnlineFileMap = new HashMap<>();
    private final Map<String, String> innerReleaseFileMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            innerCiFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("gitlab/cifile/java/.gitlab-ci.yml").getInputStream()));
            innerDockerDailyFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("gitlab/cifile/java/Dockerfile_daily").getInputStream()));
            innerDockerStageFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("gitlab/cifile/java/Dockerfile_stage").getInputStream()));
            innerDockerOnlineFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("gitlab/cifile/java/Dockerfile_online").getInputStream()));
            innerReleaseFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("gitlab/cifile/java/app.release").getInputStream()));
            log.info("初始化Gitlab CI文件成功");
        } catch (IOException e) {
            log.error("初始化Gitlab CI文件失败", e);
        }
    }

    public String getCiFileContent(String language) {
        return innerCiFileMap.getOrDefault(language, null);
    }

    public String getDockerfileContent(String language, GlobalEnvEnum envEnum) {
        switch (envEnum) {
            case Daily:
                return innerDockerDailyFileMap.getOrDefault(language, "");
            case Stage:
                return innerDockerStageFileMap.getOrDefault(language, "");
            case Online:
                return innerDockerOnlineFileMap.getOrDefault(language, "");
            default:
                return null;
        }
    }

    public String getReleaseFileContent(String language) {
        return innerReleaseFileMap.getOrDefault(language, "");
    }
}
