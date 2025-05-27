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
package cn.odboy.app.service.app;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.app.constant.AppCreateModeEnum;
import cn.odboy.app.controller.vo.AppCreateArgs;
import cn.odboy.app.controller.vo.AppModifyMetaArgs;
import cn.odboy.app.dal.dataobject.AopsAppDO;
import cn.odboy.app.dal.mysql.AopsAppMapper;
import cn.odboy.app.framework.gitlab.core.constant.GitlabDefaultConst;
import cn.odboy.app.framework.gitlab.core.repository.GitlabProjectRepository;
import cn.odboy.app.framework.gitlab.core.vo.GitlabProjectCreateArgs;
import cn.odboy.app.framework.gitlab.core.vo.GitlabProjectCreateVo;
import cn.odboy.app.framework.kubernetes.core.vo.CustomArgsAppNameVo;
import cn.odboy.common.exception.BadRequestException;
import cn.odboy.common.model.PageArgs;
import cn.odboy.common.model.PageResult;
import cn.odboy.common.model.DeleteByIdArgs;
import cn.odboy.core.framework.mybatisplus.mybatis.core.util.AnyQueryTool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.models.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 应用 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-05-07
 */
@Service
@RequiredArgsConstructor
public class AopsAppServiceImpl implements AopsAppService {
    private final AopsAppMapper currentMapper;
    private final GitlabProjectRepository gitlabProjectRepository;

    @Override
    public PageResult<AopsAppDO> describeAppPage(PageArgs<AopsAppDO> args) {
        LambdaQueryWrapper<AopsAppDO> wrapper = new LambdaQueryWrapper<>();
        AopsAppDO queryParams = args.getArgs();
        if (queryParams != null) {
            wrapper.like(StrUtil.isNotBlank(queryParams.getAppName()), AopsAppDO::getAppName, queryParams.getAppName());
            wrapper.eq(StrUtil.isNotBlank(queryParams.getAppLevel()), AopsAppDO::getAppLevel, queryParams.getAppLevel());
        }
        return AnyQueryTool.selectPageResult(currentMapper, args, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApp(AppCreateArgs args) throws Exception {
        AopsAppDO copied = BeanUtil.copyProperties(args, AopsAppDO.class);
        CustomArgsAppNameVo customArgsAppNameVo = new CustomArgsAppNameVo(copied.getAppName());
        if (!AppCreateModeEnum.NEW.getCode().equals(args.getMode())) {
            if (args.getGitProjectId() == null) {
                throw new BadRequestException("仓库地址必填");
            }
            Project project = gitlabProjectRepository.describeProjectByProjectId(args.getGitProjectId());
            copied.setGitDefaultBranch(project.getDefaultBranch());
            copied.setGitCreateAt(project.getCreatedAt());
            copied.setGitProjectId(project.getId());
            copied.setGitRepoUrl(project.getHttpUrlToRepo());
        } else {
            GitlabProjectCreateArgs createArgs = new GitlabProjectCreateArgs();
            createArgs.setName(customArgsAppNameVo.getValue());
            createArgs.setAppName(customArgsAppNameVo.getValue());
            createArgs.setGroupOrUserId(GitlabDefaultConst.ROOT_NAMESPACE_ID);
            createArgs.setDescription(args.getDescription());
            GitlabProjectCreateVo project = gitlabProjectRepository.createProject(createArgs);
            copied.setGitDefaultBranch(project.getDefaultBranch());
            copied.setGitCreateAt(project.getCreatedAt());
            copied.setGitProjectId(project.getProjectId());
            copied.setGitRepoUrl(project.getHttpUrlToRepo());
        }
        currentMapper.insert(copied);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApp(DeleteByIdArgs args) {
        currentMapper.deleteById(args.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApp(AppModifyMetaArgs args) {
        AopsAppDO copied = BeanUtil.copyProperties(args, AopsAppDO.class);
        currentMapper.updateById(copied);
    }

    @Override
    public long getAppCountByProductLineId(Long id) {
        return currentMapper.selectCount(new LambdaQueryWrapper<AopsAppDO>()
                .eq(AopsAppDO::getProductLineId, id)
        );
    }
}
