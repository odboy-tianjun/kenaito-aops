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
package cn.odboy.app.framework.gitlab.core.callback;


import cn.odboy.app.framework.gitlab.core.vo.GitlabPipelineJobInfoVo;

/**
 * 流水线任务执行回调
 *
 * @author odboy
 * @date 2025-01-17
 */
public interface GitlabPipelineJobExecuteCallback {
    void execute(GitlabPipelineJobInfoVo info);
}
