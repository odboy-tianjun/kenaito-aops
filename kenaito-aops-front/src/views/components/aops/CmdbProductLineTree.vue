<template>
  <div>
    <el-input
      v-if="filterLocal"
      v-model="filterText"
      placeholder="请输入关键字"
    />
    <el-tree
      ref="tree"
      node-key="id"
      :expand-on-click-node="false"
      :data="data"
      :props="defaultProps"
      default-expand-all
      :filter-node-method="filterNode"
      @node-click="handleNodeClick"
    />
  </div>
</template>
<script>

import * as productLineService from '@/api/aops/cmdb/productLineConfig'

export default {
  name: 'CmdbProductLineTree',
  props: {
    filterLocal: {
      type: Boolean,
      required: true,
      default: true
    }
  },
  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      filterText: '',
      data: []
    }
  },
  mounted() {
    this.handleTreeInit()
  },
  methods: {
    handleTreeInit() {
      const _this = this
      productLineService.describeProductLineTree().then(data => {
        _this.data = data
      })
    },
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    handleNodeClick(node, data, component) {
      this.$emit('handleNodeClick', node)
    }
  }
}
</script>
