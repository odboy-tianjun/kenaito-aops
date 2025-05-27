<template>
  <div class="app-container">
    <el-row>
      <el-col :span="6">
        <CmdbProductLineTree />
      </el-col>
      <el-col :span="30">
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CmdbProductLineTree from '@/views/components/aops/CmdbProductLineTree.vue'

export default {
  name: 'AopsAppRelationManage',
  components: { CmdbProductLineTree },
  data() {
    return {
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
    }
  }
}
</script>

