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
package cn.odboy.app.framework.gitlab.core.util;

import cn.odboy.common.exception.BadRequestException;
import org.gitlab4j.api.models.MergeRequest;

/**
 * Gitlab合并请求 工具
 *
 * @author odboy
 * @date 2025-05-08
 */
public class GitlabMergeRequestUtil {
    /**
     * 合并请求是否存在冲突
     *
     * @param mergeRequest /
     * @return /
     */
    public static boolean hasConflict(MergeRequest mergeRequest) {
        if (mergeRequest == null) {
            throw new BadRequestException("参数mergeRequest不能为空");
        }
        return "opened".equals(mergeRequest.getState()) && "conflict".equals(mergeRequest.getDetailedMergeStatus()) && mergeRequest.getHasConflicts();
    }

    /**
     * 合并请求是否可以Accept
     *
     * @param mergeRequest /
     * @return /
     */
    public static boolean canAccept(MergeRequest mergeRequest) {
        if (mergeRequest == null) {
            throw new BadRequestException("参数mergeRequest不能为空");
        }
        return "opened".equals(mergeRequest.getState()) && "mergeable".equals(mergeRequest.getDetailedMergeStatus()) && !mergeRequest.getHasConflicts();
    }

    /**
     * 合并请求是否已经合并
     *
     * @param mergeRequest /
     * @return /
     */
    public static boolean isMerged(MergeRequest mergeRequest) {
        if (mergeRequest == null) {
            throw new BadRequestException("参数mergeRequest不能为空");
        }
        return "merged".equals(mergeRequest.getState()) && "not_open".equals(mergeRequest.getDetailedMergeStatus());
    }

    /**
     * 正在检查是否可以合并
     *
     * @param mergeRequest /
     * @return /
     */
    public static boolean isChecking(MergeRequest mergeRequest) {
        if (mergeRequest == null) {
            throw new BadRequestException("参数mergeRequest不能为空");
        }
        return "opened".equals(mergeRequest.getState()) && "checking".equals(mergeRequest.getDetailedMergeStatus());
    }

}
