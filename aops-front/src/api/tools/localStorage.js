import request from '@/utils/request'

export function add(data) {
  return request({
    url: 'api/localStorage/uploadFile',
    method: 'post',
    data
  })
}

export function del(ids) {
  return request({
    url: 'api/localStorage/removeFileByIds',
    method: 'post',
    data: ids
  })
}

export function edit(data) {
  return request({
    url: 'api/localStorage/modifyLocalStorageById',
    method: 'post',
    data
  })
}

export default { add, edit, del }
