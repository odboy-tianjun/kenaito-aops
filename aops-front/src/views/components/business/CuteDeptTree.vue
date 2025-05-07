<template>
  <el-col :xs="9" :sm="6" :md="5" :lg="4" :xl="4">
    <div class="head-container">
      <el-input
        v-model="deptName"
        clearable
        size="small"
        placeholder="输入部门名称搜索"
        prefix-icon="el-icon-search"
        class="filter-item"
        @input="getDeptDatas"
      />
    </div>
    <el-tree
      :data="deptData"
      :load="getDeptData"
      :props="defaultProps"
      :expand-on-click-node="false"
      lazy
      @node-click="handleNodeClick"
    />
  </el-col>
</template>

<script>

import { describeDeptList } from '@/api/system/dept'

export default {
  name: 'CuteDeptTree',
  props: {
    value: {
      type: Object,
      required: false,
      default: null
    }
  },
  data() {
    return {
      deptName: '',
      deptData: [],
      defaultProps: { children: 'children', label: 'name', isLeaf: 'leaf' },
      currentDept: null
    }
  },
  watch: {
    // 监听currentDept变量
    currentDept(newValue) {
      this.$emit('input', newValue)
    }
  },
  methods: {
    getDeptData(node, resolve) {
      const params = {}
      if (typeof node !== 'object') {
        if (node) {
          params['name'] = node
        }
      } else if (node.level !== 0) {
        params['pid'] = node.data.id
      }
      setTimeout(() => {
        describeDeptList(params).then(res => {
          if (resolve) {
            resolve(res.content)
          } else {
            this.deptDatas = res.content
          }
        })
      }, 100)
    },
    handleNodeClick(data) {
      this.$emit('node-click', data)
      this.$emit('input', data)
    }
  }
}
</script>

