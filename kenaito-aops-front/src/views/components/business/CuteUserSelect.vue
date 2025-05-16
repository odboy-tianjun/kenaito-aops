<template>
  <el-select
    v-model="users"
    style="width: 100%"
    multiple
    filterable
    remote
    reserve-keyword
    placeholder="支持用户姓名、手机号、邮箱、用户名查询"
    :remote-method="remoteMethod"
    :loading="loading"
    @change="onChange"
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>

<script>

import UserService from '@/api/system/user'

export default {
  name: 'CuteUserSelect',
  props: {
    value: {
      type: Array,
      required: false,
      default: null
    }
  },
  data() {
    return {
      users: this.value,
      loading: false,
      options: []
    }
  },
  watch: {
    // 监听users变量
    users(newValue) {
      this.$emit('input', newValue)
    }
  },
  methods: {
    remoteMethod(query) {
      if (query !== '') {
        this.loading = true
        setTimeout(() => {
          this.loading = false
          this.queryUserMetaPage(query)
        }, 200)
      } else {
        this.options = []
      }
    },
    queryUserMetaPage(query, options) {
      const that = this
      UserService.queryUserMetaPage(query).then(res => {
        that.options = res
      })
    },
    onChange(value) {
      this.$emit('change', value)
      this.$emit('input', value)
    }
  }
}
</script>

