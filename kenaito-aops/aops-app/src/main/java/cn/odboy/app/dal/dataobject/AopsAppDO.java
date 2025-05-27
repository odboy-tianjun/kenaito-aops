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
package cn.odboy.app.dal.dataobject;

import cn.odboy.common.model.MyLogicEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Getter
@Setter
@ToString
@TableName("aops_app")
@ApiModel(value = "AopsApp对象", description = "应用")
public class AopsAppDO extends MyLogicEntity {
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("app_name")
    @ApiModelProperty("应用名称")
    private String appName;
    @ApiModelProperty("产品线")
    @TableField("product_line_id")
    private Long productLineId;
    @ApiModelProperty("开发语言")
    @TableField("`language`")
    private String language;
    @ApiModelProperty("应用描述")
    @TableField("`description`")
    private String description;
    @TableField("app_level")
    @ApiModelProperty("应用等级")
    private String appLevel;
    @TableField("git_repo_url")
    @ApiModelProperty("git仓库地址")
    private String gitRepoUrl;
    @ApiModelProperty("git项目id")
    @TableField("git_project_id")
    private Long gitProjectId;
    @ApiModelProperty("git项目默认分支")
    @TableField("git_default_branch")
    private String gitDefaultBranch;
    @TableField("git_create_at")
    @ApiModelProperty("git项目创建时间")
    private Date gitCreateAt;
}
