<template>
  <div class="app-container">
    <el-card>
      <el-container>
        <el-header>
          <el-form ref="form" :inline="true" :model="searchForm">
            <el-form-item label="集群名称" prop="clusterName">
              <el-input v-model="searchForm.clusterName" placeholder="请输入集群名称" />
            </el-form-item>
            <el-form-item label="所属环境" prop="envCode">
              <el-select v-model="searchForm.envCode" placeholder="请选择所属环境" clearable>
                <el-option
                  v-for="item in metadata.Env"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleFormSearch">查询</el-button>
              <el-button style="margin-left: 10pt" @click="handleFormReset('form')">重置</el-button>
            </el-form-item>
          </el-form>
        </el-header>
        <el-divider />
        <el-main>
          <el-table
            v-loading="table.loading"
            :data="table.data"
            height="250"
            max-height="250"
            border
            stripe
            style="width: 100%"
          >
            <el-table-column prop="clusterName" label="集群名称" />
            <el-table-column prop="clusterCode" label="集群编码" />
            <el-table-column prop="envCode" label="所属环境" :formatter="formatterEnvCode" />
            <el-table-column prop="clusterConfigContent" label="集群Yaml">
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.clusterConfigContent"
                  type="text"
                  @click="handleShowDetailDialog('集群Yaml内容', scope.row.clusterConfigContent)"
                >查看内容
                </el-button>
                <span v-else>无</span>
              </template>
            </el-table-column>
            <el-table-column prop="clusterNodeSize" label="集群节点数量" />
            <el-table-column prop="clusterPodSize" label="集群Pod数量" />
            <el-table-column prop="clusterDefaultAppYaml" label="应用负载Yaml">
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.clusterDefaultAppYaml"
                  type="text"
                  @click="handleShowDetailDialog('应用负载Yaml内容', scope.row.clusterDefaultAppYaml)"
                >查看内容
                </el-button>
                <span v-else>无</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="是否启用" :formatter="formatterStatus" />
            <el-table-column fixed="right" label="操作" width="260">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click.native.prevent="handleModifyClusterConfig(scope.row)">
                  编辑集群
                </el-button>
                <el-button
                  type="text"
                  size="small"
                  @click.native.prevent="handleModifyClusterDefaultAppYaml(scope.row)"
                >编辑应用负载Yml
                </el-button>
                <el-button type="text" size="small" @click.native.prevent="handleRemoveClusterConfig(scope.row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-main>
        <el-footer>
          <el-pagination
            :current-page.sync="pagination.page"
            :page-sizes="[10, 20, 30, 40, 50, 100]"
            :page-size="pagination.pageSize"
            layout="total,sizes, prev, pager, next"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </el-footer>
      </el-container>
    </el-card>
    <el-dialog :title="dialogs.detail.title" :visible.sync="dialogs.detail.visible" width="70%">
      <YamlEdit :value="dialogs.detail.content" height="500px" :read-only="true" />
    </el-dialog>
    <el-dialog title="编辑集群" :visible.sync="dialogs.clusterConfig.visible" width="70%">
      <el-form :model="dialogs.clusterConfig.from">
        <el-form-item label="集群名称" :label-width="dialogs.clusterConfig.labelWith">
          <el-input v-model="dialogs.clusterConfig.from.clusterName" autocomplete="off" />
        </el-form-item>
        <el-form-item label="集群编码" :label-width="dialogs.clusterConfig.labelWith">
          <el-input v-model="dialogs.clusterConfig.from.clusterCode" autocomplete="off" />
        </el-form-item>
        <el-form-item label="所属环境" :label-width="dialogs.clusterConfig.labelWith">
          <el-select v-model="dialogs.clusterConfig.from.envCode" placeholder="请选择所属环境">
            <el-option
              v-for="item in metadata.Env"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用" :label-width="dialogs.clusterConfig.labelWith">
          <el-switch v-model="dialogs.clusterConfig.from.status" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="集群配置" :label-width="dialogs.clusterConfig.labelWith">
          <YamlEdit
            v-model="dialogs.clusterConfig.from.clusterConfigContent"
            height="500px"
            :read-only="false"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogs.clusterConfig.visible = false">取 消</el-button>
        <el-button type="primary" @click="dialogs.clusterConfig.visible = false">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="编辑应用负载Yml" :visible.sync="dialogs.clusterConfig.appYamlVisible" width="70%">
      <el-form :model="dialogs.clusterConfig.from">
        <el-form-item>
          <YamlEdit
            v-model="dialogs.clusterConfig.from.clusterDefaultAppYaml"
            height="500px"
            :read-only="false"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogs.clusterConfig.appYamlVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogs.clusterConfig.appYamlVisible = false">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as kubernetesClusterConfigService from '@/api/aops/cmdb/kubernetesClusterConfig'
import * as metadataService from '@/api/aops/cmdb/metadata'
import { MessageUtil } from '@/utils/myElement'
import YamlEdit from '@/components/YamlEdit/index'
import { LabelUtil } from '@/utils/myUtil'

export default {
  name: 'AopsCmdbClusterConfig',
  components: { YamlEdit },
  data() {
    return {
      metadata: {
        EnableStatus: [],
        Env: [],
        AppLevel: [],
        AppLanguage: [],
        AppProductLineRole: [],
        AppRole: [],
        KubernetesNetworkType: [],
        KubernetesPodStatus: []
      },
      searchForm: {
        clusterName: '',
        envCode: ''
      },
      table: {
        data: [],
        loading: false
      },
      pagination: {
        page: 1,
        pageSize: 10,
        total: 0
      },
      dialogs: {
        detail: {
          visible: false,
          title: '',
          content: ''
        },
        clusterConfig: {
          visible: false,
          appYamlVisible: false,
          labelWith: '120px',
          from: {
            id: null,
            clusterName: null,
            clusterCode: null,
            envCode: null,
            clusterConfigContent: null,
            clusterDefaultAppYaml: null,
            status: null
          }
        }
      }
    }
  },
  mounted() {
    this.initMetadata()
    this.pagination.page = 1
    this.handleFormSearch(this.pagination.page, this.pagination.pageSize, this.searchForm)
  },
  methods: {
    initMetadata() {
      // 初始化枚举
      const _this = this
      metadataService.describeEnableStatusMetadataOptions().then(data => { _this.metadata.EnableStatus = data })
      metadataService.describeEnvMetadataOptions().then(data => { _this.metadata.Env = data })
      metadataService.describeAppLevelMetadataOptions().then(data => { _this.metadata.AppLevel = data })
      metadataService.describeAppLanguageMetadataOptions().then(data => { _this.metadata.AppLanguage = data })
      metadataService.describeAppProductLineRoleMetadataOptions().then(data => { _this.metadata.AppProductLineRole = data })
      metadataService.describeAppRoleMetadataOptions().then(data => { _this.metadata.AppRole = data })
      metadataService.describeKubernetesNetworkTypeMetadataOptions().then(data => { _this.metadata.KubernetesNetworkType = data })
      metadataService.describeKubernetesPodStatusMetadataOptions().then(data => { _this.metadata.KubernetesPodStatus = data })
    },
    handleFormSearch(page, pageSize, args) {
      // 初始化表格数据
      const _this = this
      _this.table.loading = true
      kubernetesClusterConfigService.describePage(page, pageSize, args).then(data => {
        _this.table.data = data.content
        _this.pagination.total = data.totalElements
        _this.table.loading = false
      }).finally(() => {
        _this.table.loading = false
      })
    },
    handleFormReset(formName) {
      this.$refs[formName].resetFields()
    },
    formatterEnvCode(row, column, cellValue, index) {
      return LabelUtil.getLabel(this.metadata.Env, cellValue)
    },
    formatterStatus(row, column, cellValue, index) {
      return LabelUtil.getLabel(this.metadata.EnableStatus, cellValue)
    },
    handleShowDetailDialog(title, value) {
      this.dialogs.detail.title = title
      this.dialogs.detail.content = value
      this.dialogs.detail.visible = true
    },
    handleModifyClusterConfig(values) {
      MessageUtil.warning(this, '功能开发中')
      this.dialogs.clusterConfig.from = { ...values }
      this.dialogs.clusterConfig.visible = true
      console.error('handleModifyClusterConfig(values)', this.dialogs.clusterConfig.from)
    },
    handleModifyClusterDefaultAppYaml(values) {
      MessageUtil.warning(this, '功能开发中')
      this.dialogs.clusterConfig.from = { ...values }
      this.dialogs.clusterConfig.appYamlVisible = true
      console.error('handleModifyClusterDefaultAppYaml(values)', this.dialogs.clusterConfig.from)
    },
    handleRemoveClusterConfig(values) {
      MessageUtil.warning(this, '功能开发中')
      this.dialogs.clusterConfig.from = { ...values }
      console.error('handleRemoveClusterConfig(values)', this.dialogs.clusterConfig.from)
    },
    handleCurrentChange(page) {
      this.pagination.page = page
      this.handleFormSearch(this.pagination.page, this.pagination.pageSize, this.searchForm)
    },
    handleSizeChange(size) {
      this.pagination.page = 1
      this.pagination.pageSize = size
      this.handleFormSearch(this.pagination.page, this.pagination.pageSize, this.searchForm)
    }
  }
}
</script>

