package cn.odboy.framework.gitlab.model;

import cn.odboy.base.MyObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gitlab4j.api.models.Changes;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Repository;
import java.util.List;

/**
 * Gitlab 系统钩子事件回调 发起合并分支请求
 * @author odboy
 * @date 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GitlabSystemHookMergeRequestEventResponse extends MyObject {
    /**
     * eg. merge_request
     */
    @JSONField(name = "event_type")
    private String eventType;
    @JSONField(name = "object_attributes")
    private org.gitlab4j.api.webhook.MergeRequestEvent.ObjectAttributes objectAttributes;
    @JSONField(name = "changes")
    private Changes changes;
    @JSONField(name = "project")
    private Project project;
    @JSONField(name = "repository")
    private Repository repository;
    @JSONField(name = "user")
    private org.gitlab4j.api.models.User user;
    @JSONField(name = "object_kind")
    private String objectKind;
    @JSONField(name = "labels")
    private List<String> labels;
}
