package cn.odboy.app.framework.gitlab.model;

import cn.odboy.common.pojo.MyObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gitlab4j.api.models.Changes;
import java.util.List;

/**
 * Gitlab 系统钩子事件回调 仓库更新
 *
 * @author odboy
 * @date 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GitlabSystemHookProjectUpdateEventResponse extends MyObject {
    /**
     * eg. http://example.com/avatar/user.png
     */
    @JSONField(name = "user_avatar")
    private String userAvatar;
    /**
     * eg. test@example.com
     */
    @JSONField(name = "user_email")
    private String userEmail;
    /**
     * eg. 10
     */
    @JSONField(name = "user_id")
    private Integer userId;
    /**
     * eg. 40
     */
    @JSONField(name = "project_id")
    private Integer projectId;
    /**
     * eg. ["refs/heads/master"]
     */
    @JSONField(name = "refs")
    private List<String> refs;
    /**
     * eg. john.doe
     */
    @JSONField(name = "user_name")
    private String userName;
    /**
     * eg. [{"ref":"refs/heads/master","before":"8205ea8d81ce0c6b90fbe8280d118cc9fdad6130","after":"4045ea7a3df38697b3730a20fb73c8bed8a3e69e"}]
     */
    @JSONField(name = "changes")
    private List<Changes> changes;
    /**
     * repository_update
     */
    @JSONField(name = "event_name")
    private String eventName;
}
