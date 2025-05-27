<template>
  <div class="app-container">
    <div class="head-container">
      <el-form ref="form" :inline="true" :model="searchParams">
        <el-form-item label="集群名称" prop="clusterName">
          <el-input v-model="searchParams.clusterName" placeholder="请输入集群名称" />
        </el-form-item>
        <el-form-item label="环境" prop="envCode">
          <el-select v-model="searchParams.envCode" placeholder="请选择环境" clearable>
            <el-option
              v-for="item in metadata.Env"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button class="filter-item" size="mini" type="success" icon="el-icon-search" @click="handleFormSearch(1,pageable.pageSize,searchParams)">搜索</el-button>
          <el-button class="filter-item" size="mini" type="warning" icon="el-icon-refresh-left" @click="handleFormReset('form')">重置</el-button>
          <el-button class="filter-item" size="mini" type="primary" icon="el-icon-plus" @click="handleCreateClusterConfig">新增</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-table
      v-loading="table.loading"
      :data="table.data"
      height="250"
      max-height="250"
      stripe
      style="width: 100%"
    >
      <el-table-column prop="clusterName" label="集群名称" />
      <el-table-column prop="clusterCode" label="集群编码" />
      <el-table-column prop="envCode" label="环境" :formatter="formatterEnvCode" />
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
      <el-table-column prop="clusterDefaultAppImage" label="初始镜像地址" />
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
      <el-table-column fixed="right" label="操作" width="240">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click.native.prevent="handleUpdateClusterConfig(scope.row)">
            修改集群
          </el-button>
          <el-button
            type="text"
            size="small"
            @click.native.prevent="handleModifyClusterDefaultAppYaml(scope.row)"
          >修改应用负载Yml
          </el-button>
          <el-button type="text" size="small" @click.native.prevent="handleRemoveClusterConfig(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page.sync="pageable.page"
      :page-sizes="[10, 20, 30, 40, 50, 100]"
      :page-size="pageable.pageSize"
      layout="total,sizes, prev, pager, next"
      :total="pageable.total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <el-drawer :title="drawers.detail.title" :visible.sync="drawers.detail.visible" size="50%">
      <YamlEdit :value="drawers.detail.content" height="500px" :read-only="true" />
    </el-drawer>
    <el-drawer :title="`${drawers.clusterConfig.modeMap[drawers.clusterConfig.currentMode]}集群`" :visible.sync="drawers.clusterConfig.visible" size="50%">
      <el-form ref="clusterConfigFormRef" :model="drawers.clusterConfig.form" :rules="drawers.clusterConfig.rules">
        <el-form-item prop="clusterName" label="集群名称" :label-width="drawers.clusterConfig.labelWith">
          <el-input v-model="drawers.clusterConfig.form.clusterName" autocomplete="off" />
        </el-form-item>
        <el-form-item prop="clusterCode" label="集群编码" :label-width="drawers.clusterConfig.labelWith">
          <el-input v-model="drawers.clusterConfig.form.clusterCode" autocomplete="off" />
        </el-form-item>
        <el-form-item prop="envCode" label="环境" :label-width="drawers.clusterConfig.labelWith">
          <el-select v-model="drawers.clusterConfig.form.envCode" placeholder="请选择环境">
            <el-option
              v-for="item in metadata.Env"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用" :label-width="drawers.clusterConfig.labelWith">
          <el-switch v-model="drawers.clusterConfig.form.status" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item prop="clusterDefaultAppImage" label="初始镜像地址" :label-width="drawers.clusterConfig.labelWith">
          <el-input v-model="drawers.clusterConfig.form.clusterDefaultAppImage" autocomplete="off" />
        </el-form-item>
        <el-form-item prop="clusterConfigContent" label="集群配置" :label-width="drawers.clusterConfig.labelWith">
          <YamlEdit
            v-model="drawers.clusterConfig.form.clusterConfigContent"
            height="500px"
            :read-only="false"
          />
        </el-form-item>
      </el-form>
      <div style="text-align: right;padding: 10pt 20pt 10pt 10pt;">
        <el-button @click="drawers.clusterConfig.visible = false">取 消</el-button>
        <el-button
          type="primary"
          @click="()=>{
            if(drawers.clusterConfig.currentMode === 'add'){
              handleCreateClusterSubmit('clusterConfigFormRef')
              return
            }
            handleUpdateClusterSubmit('clusterConfigFormRef')
          }"
        >提 交</el-button>
      </div>
    </el-drawer>
    <el-drawer title="编辑应用负载Yml" :visible.sync="drawers.modifyClusterDefaultAppYaml.visible" size="50%">
      <el-form ref="clusterDefaultAppYamlFormRef" :model="drawers.modifyClusterDefaultAppYaml.form" :rules="drawers.modifyClusterDefaultAppYaml.rules">
        <el-form-item prop="clusterDefaultAppYaml">
          <YamlEdit
            v-model="drawers.modifyClusterDefaultAppYaml.form.clusterDefaultAppYaml"
            height="500px"
            :read-only="false"
          />
        </el-form-item>
      </el-form>
      <div style="text-align: right;padding: 10pt 20pt 10pt 10pt;">
        <el-button @click="drawers.modifyClusterDefaultAppYaml.visible = false">取 消</el-button>
        <el-button type="primary" @click="handleModifyAppYamlSubmit('clusterDefaultAppYamlFormRef')">确 定</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import * as kubernetesClusterConfigService from '@/api/aops/cmdb/kubernetesClusterConfig'
import * as metadataService from '@/api/aops/cmdb/metadata'
import YamlEdit from '@/components/YamlEdit/index'
import { LabelUtil } from '@/utils/myUtil'
import { MessageBoxUtil, MessageUtil } from '@/utils/myElement'

export default {
  name: 'AopsCmdbKubernetesClusterConfig',
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
        KubernetesPodStatus: [],
        ProductLine: [],
        UserInfo: []
      },
      searchParams: {
        clusterName: '',
        envCode: ''
      },
      table: {
        data: [],
        loading: false
      },
      pageable: {
        page: 1,
        pageSize: 10,
        total: 0
      },
      drawers: {
        detail: {
          visible: false,
          title: '',
          content: ''
        },
        clusterConfig: {
          visible: false,
          labelWith: '120px',
          modeMap: {
            'add': '新增',
            'edit': '编辑'
          },
          currentMode: 'add',
          form: {
            id: null,
            clusterName: null,
            clusterCode: null,
            envCode: null,
            clusterConfigContent: null,
            clusterDefaultAppImage: null,
            clusterDefaultAppYaml: null,
            status: 1
          },
          rules: {
            clusterName: [
              { required: true, message: '请输入集群名称', trigger: 'blur' }
            ],
            clusterCode: [
              { required: true, message: '请输入集群编码', trigger: 'blur' }
            ],
            envCode: [
              { required: true, message: '请选择环境', trigger: 'change' }
            ],
            clusterConfigContent: [
              { required: true, message: '请输入集群配置', trigger: 'blur' }
            ],
            clusterDefaultAppImage: [
              { required: true, message: '请输入初始镜像地址', trigger: 'blur' }
            ]
          }
        },
        modifyClusterDefaultAppYaml: {
          visible: false,
          labelWith: '120px',
          form: {
            id: null,
            clusterDefaultAppYaml: null
          },
          rules: {
            clusterDefaultAppYaml: [
              { required: true, message: '请输入应用负载Yml配置', trigger: 'blur' }
            ]
          }
        }
      }
    }
  },
  mounted() {
    this.initMetadata()
    this.handleFormSearch(1, this.pageable.pageSize, this.searchParams)
  },
  methods: {
    initMetadata() {
      // 初始化枚举
      const _this = this
      metadataService.getAll().then(data => { _this.metadata = data })
    },
    handleFormSearch(page, pageSize, args) {
      // 初始化表格数据
      const _this = this
      _this.pageable.page = page
      _this.table.loading = true
      kubernetesClusterConfigService.describeClusterConfigPage(page, pageSize, args).then(data => {
        _this.table.data = data.content
        _this.pageable.total = data.totalElements
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
      const item = LabelUtil.getItem(this.metadata.EnableStatus, cellValue)
      if (item == null) {
        return cellValue
      }
      return <el-tag effect='dark' type={item.ext.tagType}>{item.label}</el-tag>
    },
    handleShowDetailDialog(title, value) {
      this.drawers.detail.title = title
      this.drawers.detail.content = value
      this.drawers.detail.visible = true
    },
    handleUpdateClusterConfig(values) {
      this.drawers.clusterConfig.currentMode = 'edit'
      this.drawers.clusterConfig.form = { ...values }
      this.drawers.clusterConfig.visible = true
    },
    handleUpdateClusterSubmit(formName) {
      const _this = this
      _this.$refs[formName].validate((valid) => {
        if (valid) {
          kubernetesClusterConfigService.updateClusterConfig(_this.drawers.clusterConfig.form).then(data => {
            MessageUtil.success(_this, '操作成功')
            _this.drawers.clusterConfig.visible = false
            _this.handleFormSearch(1, _this.pageable.pageSize, _this.searchParams)
          })
        }
      })
    },
    handleModifyClusterDefaultAppYaml(values) {
      this.drawers.modifyClusterDefaultAppYaml.form = { ...values }
      this.drawers.modifyClusterDefaultAppYaml.visible = true
    },
    handleModifyAppYamlSubmit(formName) {
      const _this = this
      _this.$refs[formName].validate((valid) => {
        if (valid) {
          kubernetesClusterConfigService.modifyClusterDefaultAppYml(_this.drawers.modifyClusterDefaultAppYaml.form).then(data => {
            MessageUtil.success(_this, '操作成功')
            _this.drawers.modifyClusterDefaultAppYaml.visible = false
            _this.handleFormSearch(1, _this.pageable.pageSize, _this.searchParams)
          })
        }
      })
    },
    handleRemoveClusterConfig(values) {
      const _this = this
      MessageBoxUtil.deleteMessageConfirm(_this, `确认删除 ${values.clusterName} 配置吗？`,
        () => {
          kubernetesClusterConfigService.deleteClusterConfig(values).then(data => {
            MessageUtil.success(_this, '操作成功')
            _this.handleFormSearch(1, _this.pageable.pageSize, _this.searchParams)
          })
        },
        null
      )
    },
    handleCreateClusterConfig() {
      this.drawers.clusterConfig.currentMode = 'add'
      this.drawers.clusterConfig.form = {}
      this.drawers.clusterConfig.visible = true
    },
    handleCreateClusterSubmit(formName) {
      const _this = this
      _this.$refs[formName].validate((valid) => {
        if (valid) {
          const values = _this.drawers.clusterConfig.form
          values.id = null
          kubernetesClusterConfigService.createClusterConfig(values).then(data => {
            MessageUtil.success(_this, '操作成功')
            _this.drawers.clusterConfig.visible = false
            _this.handleFormSearch(1, _this.pageable.pageSize, _this.searchParams)
          })
        }
      })
    },
    handleCurrentChange(page) {
      this.handleFormSearch(page, this.pageable.pageSize, this.searchParams)
    },
    handleSizeChange(size) {
      this.pageable.pageSize = size
      this.handleFormSearch(1, this.pageable.pageSize, this.searchParams)
    }
  }
}
</script>

