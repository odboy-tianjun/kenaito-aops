<template>
  <div class="app-container">
    <el-row>
      <el-col :span="4">
        <CmdbProductLineTree ref="treePreviewRef" :filter-local="false" @handleNodeClick="handleTreeNodeClick" />
      </el-col>
      <el-col :span="20">
        <div class="head-container">
          <el-form ref="form" :inline="true" :model="searchParams">
            <el-form-item label="产品名称" prop="lineName">
              <el-input v-model="searchParams.lineName" placeholder="请输入产品名称" />
            </el-form-item>
            <el-form-item>
              <el-button class="filter-item" size="mini" type="success" icon="el-icon-search" @click="handleFormSearch(1,pageable.pageSize,searchParams)">搜索</el-button>
              <el-button class="filter-item" size="mini" type="warning" icon="el-icon-refresh-left" @click="handleFormReset('form')">重置</el-button>
              <el-button class="filter-item" size="mini" type="primary" icon="el-icon-plus" @click="handleCreateProductLineConfig">新增</el-button>
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
          <el-table-column prop="lineName" label="产品名称" />
          <el-table-column prop="remark" label="描述" />
          <el-table-column fixed="right" label="操作" width="240">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click.native.prevent="handleUpdateProductLineConfig(scope.row)">
                修改
              </el-button>
              <el-button type="text" size="small" @click.native.prevent="handleRemoveProductLineConfig(scope.row)">
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
        <el-drawer :title="`${drawers.productLineConfig.modeMap[drawers.productLineConfig.currentMode]}产品`" :visible.sync="drawers.productLineConfig.visible" size="30%">
          <el-form ref="productLineConfigFormRef" :model="drawers.productLineConfig.form" :rules="drawers.productLineConfig.rules">
            <el-form-item prop="lineName" label="产品名称" :label-width="drawers.productLineConfig.labelWith">
              <el-input v-model="drawers.productLineConfig.form.lineName" autocomplete="off" />
            </el-form-item>
            <el-form-item prop="parentId" label="父产品" :label-width="drawers.productLineConfig.labelWith">
              <el-select v-model="drawers.productLineConfig.form.parentId" placeholder="请选择父产品" :disabled="true">
                <el-option
                  v-for="item in metadata.ProductLine"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item prop="owner" label="产品负责人" :label-width="drawers.productLineConfig.labelWith">
              <el-select
                v-model="drawers.productLineConfig.form.owner"
                placeholder="请选择产品负责人"
                clearable
                filterable
                :multiple-limit="20"
                :multiple="true"
              >
                <el-option
                  v-for="item in metadata.UserInfo"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item prop="pe" label="运维负责人" :label-width="drawers.productLineConfig.labelWith">
              <el-select
                v-model="drawers.productLineConfig.form.pe"
                placeholder="请选择运维负责人"
                clearable
                filterable
                :multiple-limit="20"
                :multiple="true"
              >
                <el-option
                  v-for="item in metadata.UserInfo"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item prop="remark" label="描述" :label-width="drawers.productLineConfig.labelWith">
              <el-input v-model="drawers.productLineConfig.form.remark" autocomplete="off" />
            </el-form-item>
          </el-form>
          <div style="text-align: right;padding: 10pt 20pt 10pt 10pt;">
            <el-button @click="drawers.productLineConfig.visible = false">取 消</el-button>
            <el-button
              type="primary"
              @click="()=>{
                if(drawers.productLineConfig.currentMode === 'add'){
                  handleCreateClusterSubmit('productLineConfigFormRef')
                  return
                }
                handleUpdateClusterSubmit('productLineConfigFormRef')
              }"
            >提 交</el-button>
          </div>
        </el-drawer>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as productLineConfigService from '@/api/aops/cmdb/productLineConfig'
import * as metadataService from '@/api/aops/cmdb/metadata'
import { MessageBoxUtil, MessageUtil } from '@/utils/myElement'
import CmdbProductLineTree from '@/views/components/aops/CmdbProductLineTree.vue'

export default {
  name: 'AopsCmdbProductLineConfig',
  components: { CmdbProductLineTree },
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
        lineName: ''
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
        productLineConfig: {
          visible: false,
          labelWith: '120px',
          modeMap: {
            'add': '新增',
            'edit': '编辑'
          },
          currentMode: 'add',
          currentNode: null,
          form: {
            id: null,
            parentId: null,
            lineName: null,
            owner: [],
            pe: [],
            remark: null
          },
          rules: {
            lineName: [
              { required: true, message: '请输入产品名称', trigger: 'blur' }
            ],
            owner: [
              { required: true, message: '请选择产品负责人', trigger: 'change' }
            ],
            pe: [
              { required: true, message: '请选择运维负责人', trigger: 'change' }
            ],
            remark: [
              { required: true, message: '请输入描述', trigger: 'blur' }
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
    handleTreeNodeClick(node) {
      this.drawers.productLineConfig.currentNode = node
    },
    handleFormSearch(page, pageSize, args) {
      // 初始化表格数据
      const _this = this
      _this.pageable.page = page
      _this.table.loading = true
      productLineConfigService.describeProductLinePage(page, pageSize, args).then(data => {
        _this.table.data = data.content
        _this.pageable.total = data.totalElements
        _this.table.loading = false
      }).finally(() => {
        _this.table.loading = false
        _this.$refs.treePreviewRef.handleTreeInit()
      })
    },
    handleFormReset(formName) {
      this.$refs[formName].resetFields()
    },
    handleUpdateProductLineConfig(values) {
      this.drawers.productLineConfig.currentMode = 'edit'
      this.drawers.productLineConfig.form = {
        ...values,
        owner: JSON.parse(values.owner),
        pe: JSON.parse(values.pe)
      }
      this.drawers.productLineConfig.visible = true
    },
    handleUpdateClusterSubmit(formName) {
      const _this = this
      _this.$refs[formName].validate((valid) => {
        if (valid) {
          productLineConfigService.updateProductLine(_this.drawers.productLineConfig.form).then(data => {
            MessageUtil.success(_this, '操作成功')
            _this.drawers.productLineConfig.visible = false
            _this.handleFormSearch(1, _this.pageable.pageSize, _this.searchParams)
          })
        }
      })
    },
    handleRemoveProductLineConfig(values) {
      const _this = this
      MessageBoxUtil.deleteMessageConfirm(_this, `确认删除产品 ${values.lineName} 吗？`,
        () => {
          productLineConfigService.deleteProductLine(values).then(data => {
            MessageUtil.success(_this, '操作成功')
            _this.handleFormSearch(1, _this.pageable.pageSize, _this.searchParams)
          })
        },
        null
      )
    },
    handleCreateProductLineConfig() {
      this.drawers.productLineConfig.currentMode = 'add'
      this.drawers.productLineConfig.form = {}
      const currentNode = this.drawers.productLineConfig.currentNode
      if (currentNode) {
        if (currentNode.ext1) {
          MessageUtil.error(this, '当前产品线已绑定应用，无法新增节点')
          return
        }
        this.drawers.productLineConfig.form.parentId = currentNode.id
      } else {
        this.drawers.productLineConfig.form.parentId = 0
      }
      this.drawers.productLineConfig.visible = true
    },
    handleCreateClusterSubmit(formName) {
      const _this = this
      _this.$refs[formName].validate((valid) => {
        if (valid) {
          const values = _this.drawers.productLineConfig.form
          values.id = null
          productLineConfigService.createProductLine(values).then(data => {
            MessageUtil.success(_this, '操作成功')
            _this.drawers.productLineConfig.visible = false
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

