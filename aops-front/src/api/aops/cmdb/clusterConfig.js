import request from '@/utils/request'

export function describeKubernetesClusterConfigPage(page, size, args) {
  return request({
    url: 'api/aops/kubernetes/clusterConfig/describeKubernetesClusterConfigPage',
    method: 'post',
    data: {
      page: page,
      size: size,
      args: args
    }
  })
}
