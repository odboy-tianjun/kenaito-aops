import request from '@/utils/request'

export function describeClusterConfigPage(page, size, args) {
  return request({
    url: 'api/cmdb/kubernetes/clusterConfig/describeClusterConfigPage',
    method: 'post',
    data: {
      page: page,
      size: size,
      args: args
    }
  })
}

export function createClusterConfig(args) {
  return request({
    url: 'api/cmdb/kubernetes/clusterConfig/createClusterConfig',
    method: 'post',
    data: args
  })
}

export function updateClusterConfig(args) {
  return request({
    url: 'api/cmdb/kubernetes/clusterConfig/updateClusterConfig',
    method: 'post',
    data: args
  })
}

export function modifyClusterDefaultAppYml(args) {
  return request({
    url: 'api/cmdb/kubernetes/clusterConfig/modifyClusterDefaultAppYml',
    method: 'post',
    data: args
  })
}

export function deleteClusterConfig(args) {
  return request({
    url: 'api/cmdb/kubernetes/clusterConfig/deleteClusterConfig',
    method: 'post',
    data: args
  })
}
