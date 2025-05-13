export const Constants = {
  Global: {
    EnvCode: [
      { value: 'daily', label: '日常' },
      { value: 'stage', label: '预发' },
      { value: 'online', label: '生产' }
    ]
  },
  App: {},
  Cmdb: {
    ClusterConfigStatus: [
      { value: 1, label: '启用' },
      { value: 0, label: '禁用' }
    ]
  }
}

export function getLabel(enums, value) {
  const find = enums.find(item => item.value === value)
  if (find) {
    return find.label
  }
  return value
}
