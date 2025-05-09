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
