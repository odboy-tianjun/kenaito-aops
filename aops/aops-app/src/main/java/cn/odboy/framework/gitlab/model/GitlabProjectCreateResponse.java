package cn.odboy.framework.gitlab.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class GitlabProjectCreateResponse extends GitlabProjectCreateArgs {
    /**
     * OwnerId
     */
    private Long creatorId;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 默认分支
     */
    private String defaultBranch;
    /**
     * git clone 地址
     */
    private String httpUrlToRepo;
    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 可见级别
     */
    private String visibility;
    /**
     * 项目地址
     */
    private String homeUrl;
}
