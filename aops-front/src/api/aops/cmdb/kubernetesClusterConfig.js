import request from '@/utils/request'

export function describePage(page, size, args) {
  return request({
    url: 'api/aops/kubernetes/clusterConfig/describePage',
    method: 'post',
    data: {
      page: page,
      size: size,
      args: args
    }
  })
}

export function updateClusterConfig(args) {
  return request({
    url: 'api/aops/kubernetes/clusterConfig/updateClusterConfig',
    method: 'post',
    data: { ...args }
  })
}

export function modifyClusterDefaultAppYml(id, clusterDefaultAppYaml) {
  return request({
    url: 'api/aops/kubernetes/clusterConfig/modifyClusterDefaultAppYml',
    method: 'post',
    data: {
      id: id,
      clusterDefaultAppYaml: clusterDefaultAppYaml
    }
  })
}
