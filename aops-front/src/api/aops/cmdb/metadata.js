import request from '@/utils/request'

export function describeEnableStatusMetadataOptions() {
  return request({
    url: '/api/aops/cmdb/metadata/describeEnableStatusMetadataOptions',
    method: 'post'
  })
}
export function describeEnvMetadataOptions() {
  return request({
    url: '/api/aops/cmdb/metadata/describeEnvMetadataOptions',
    method: 'post'
  })
}
export function describeAppLevelMetadataOptions() {
  return request({
    url: '/api/aops/cmdb/metadata/describeAppLevelMetadataOptions',
    method: 'post'
  })
}
export function describeAppLanguageMetadataOptions() {
  return request({
    url: '/api/aops/cmdb/metadata/describeAppLanguageMetadataOptions',
    method: 'post'
  })
}
export function describeAppProductLineRoleMetadataOptions() {
  return request({
    url: '/api/aops/cmdb/metadata/describeAppProductLineRoleMetadataOptions',
    method: 'post'
  })
}
export function describeAppRoleMetadataOptions() {
  return request({
    url: '/api/aops/cmdb/metadata/describeAppRoleMetadataOptions',
    method: 'post'
  })
}
export function describeKubernetesNetworkTypeMetadataOptions() {
  return request({
    url: '/api/aops/cmdb/metadata/describeKubernetesNetworkTypeMetadataOptions',
    method: 'post'
  })
}
export function describeKubernetesPodStatusMetadataOptions() {
  return request({
    url: '/api/aops/cmdb/metadata/describeKubernetesPodStatusMetadataOptions',
    method: 'post'
  })
}

