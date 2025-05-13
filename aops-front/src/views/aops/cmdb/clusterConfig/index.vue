<template>
  <div class="app-container">
    <el-card>
      <el-container>
        <el-header>
          <el-form ref="form" :inline="true" :model="form">
            <el-form-item label="集群名称" prop="clusterName">
              <el-input v-model="form.clusterName" placeholder="请输入集群名称" />
            </el-form-item>
            <el-form-item label="所属环境" prop="envCode">
              <el-select v-model="form.envCode" placeholder="请选择所属环境" clearable>
                <el-option
                  v-for="item in Constants.Global.EnvCode"
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
            :data="tableData"
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
                <el-button v-if="scope.row.clusterConfigContent" type="text" @click="handleShowDetail('集群Yaml内容', scope.row.clusterConfigContent)">查看内容</el-button>
                <span v-else>无</span>
              </template>
            </el-table-column>
            <el-table-column prop="clusterNodeSize" label="集群节点数量" />
            <el-table-column prop="clusterPodSize" label="集群Pod数量" />
            <el-table-column prop="clusterDefaultAppYaml" label="应用负载Yaml">
              <template slot-scope="scope">
                <el-button v-if="scope.row.clusterDefaultAppYaml" type="text" @click="handleShowDetail('应用负载Yaml内容', scope.row.clusterDefaultAppYaml)">查看内容</el-button>
                <span v-else>无</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="是否启用" :formatter="formatterStatus" />
            <el-table-column fixed="right" label="操作" width="260">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click.native.prevent="handleModifyClusterConfigContent(scope.row)">更新集群Yaml</el-button>
                <el-button type="text" size="small" @click.native.prevent="handleModifyClusterDefaultAppYaml(scope.row)">更新应用负载Yaml</el-button>
                <el-button type="text" size="small" @click.native.prevent="handleRemoveClusterConfig(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-main>
        <el-footer>
          <el-pagination
            :current-page.sync="currentPage"
            :page-sizes="[10, 20, 30, 40, 50, 100]"
            :page-size="currentPageSize"
            layout="total,sizes, prev, pager, next"
            :total="currentTotal"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </el-footer>
      </el-container>
    </el-card>
    <el-dialog :title="detailTitle" :visible.sync="detailVisible" width="70%">
      <YamlEdit :value="detailContent" height="500px" :read-only="true" />
    </el-dialog>
    <el-dialog title="更新集群Yaml" :visible.sync="modifyClusterConfigVisible" width="70%">
      <YamlEdit
        :value="modifyClusterConfigContent"
        height="500px"
        :read-only="false"
        @changed="handleModifyClusterConfigContentChange"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="modifyClusterConfigVisible = false">取 消</el-button>
        <el-button type="primary" @click="doModifyClusterConfig">提 交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as clusterConfigService from '@/api/aops/cmdb/clusterConfig'
import { Constants, getLabel } from '../../constants'
import { MessageUtil } from '@/utils/myElement'
import YamlEdit from '@/components/YamlEdit/index'

export default {
  name: 'AopsCmdbClusterConfig',
  components: { YamlEdit },
  data() {
    return {
      form: {
        clusterName: '',
        envCode: ''
      },
      tableData: [],
      currentPage: 1,
      currentPageSize: 10,
      currentTotal: 0,
      currentControlRow: {},
      detailVisible: false,
      detailTitle: '',
      detailContent: '',
      modifyClusterConfigVisible: false,
      modifyClusterConfigContent: ''
    }
  },
  computed: {
    Constants() {
      return Constants
    }
  },
  mounted() {
    this.handleFormSearch()
  },
  methods: {
    getLabel,
    handleFormSearch() {
      const _this = this
      _this.currentPage = 1
      clusterConfigService.describeKubernetesClusterConfigPage(_this.currentPage, _this.currentPageSize, _this.form).then(data => {
        _this.tableData = data.content
        _this.currentTotal = data.totalElements
      })
    },
    handleFormReset(formName) {
      this.$refs[formName].resetFields()
    },
    formatterEnvCode(row, column, cellValue, index) {
      return getLabel(Constants.Global.EnvCode, cellValue)
    },
    formatterStatus(row, column, cellValue, index) {
      return getLabel(Constants.Cmdb.ClusterConfigStatus, cellValue)
    },
    handleShowDetail(title, value) {
      this.detailTitle = title
      this.detailContent = value
      this.detailVisible = true
    },
    handleModifyClusterConfigContent(row) {
      this.currentControlRow = { ...row }
      this.modifyClusterConfigContent = String(row.clusterConfigContent)
      this.modifyClusterConfigVisible = true
    },
    handleModifyClusterConfigContentChange(value) {
      this.modifyClusterConfigContent = value
    },
    doModifyClusterConfig() {
      MessageUtil.warning(this, '功能开发中')
      console.log('modifyClusterConfigObj', this.currentControlRow)
      console.log('modifyClusterConfigContent', this.modifyClusterConfigContent)
    },
    handleModifyClusterDefaultAppYaml(row) {
      MessageUtil.warning(this, '功能开发中')
    },
    handleRemoveClusterConfig(row) {
      MessageUtil.warning(this, '功能开发中')
    },
    handleSizeChange(size) {
      this.currentPageSize = size
    },
    handleCurrentChange(page) {
      this.currentPage = page
    }
  }
}
</script>

