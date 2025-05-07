import request from '@/utils/request'

export function add(data) {
  return request({
    url: 'api/jobs',
    method: 'post',
    data
  })
}

export function del(ids) {
  return request({
    url: 'api/jobs',
    method: 'delete',
    data: ids
  })
}

export function edit(data) {
  return request({
    url: 'api/jobs',
    method: 'put',
    data
  })
}

export function switchQuartzJobStatus(id) {
  return request({
    url: 'api/jobs/switchQuartzJobStatus/' + id,
    method: 'post'
  })
}

export function startQuartzJob(id) {
  return request({
    url: 'api/jobs/startQuartzJob/' + id,
    method: 'post'
  })
}

export default { add, edit, del, switchQuartzJobStatus, startQuartzJob }
