import request from '@/utils/request'

export function getAll() {
  return request({
    url: '/api/cmdb/metadata/getAll',
    method: 'post'
  })
}
