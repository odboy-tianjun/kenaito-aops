import request from '@/utils/request'

// export function getDicts() {
//   return request({
//     url: 'api/dict/queryAllDict',
//     method: 'post'
//   })
// }

export function add(data) {
  return request({
    url: 'api/dict/saveDict',
    method: 'post',
    data
  })
}

export function del(ids) {
  return request({
    url: 'api/dict/removeDictByIds',
    method: 'post',
    data: ids
  })
}

export function edit(data) {
  return request({
    url: 'api/dict/modifyDictById',
    method: 'post',
    data
  })
}

export default { add, edit, del }
