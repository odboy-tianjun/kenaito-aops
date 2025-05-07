import request from '@/utils/request'

export function get(dictName) {
  const params = {
    dictName,
    page: 0,
    size: 9999
  }
  return request({
    url: 'api/dictDetail',
    method: 'get',
    params
  })
}

// export function getDictMap(dictName) {
//   const params = {
//     dictName,
//     page: 0,
//     size: 9999
//   }
//   return request({
//     url: 'api/dictDetail/getDictDetailMaps',
//     method: 'get',
//     params
//   })
// }

export function add(data) {
  return request({
    url: 'api/dictDetail/saveDictDetail',
    method: 'post',
    data
  })
}

export function del(id) {
  return request({
    url: 'api/dictDetail/removeDictDetailById',
    method: 'post',
    data: { id: id }
  })
}

export function edit(data) {
  return request({
    url: 'api/dictDetail/modifyDictDetailById',
    method: 'post',
    data
  })
}

export default { add, edit, del }
