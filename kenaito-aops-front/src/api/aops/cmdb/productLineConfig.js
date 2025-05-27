import request from '@/utils/request'

export function describeProductLineTree() {
  return request({
    url: 'api/cmdb/productLine/describeProductLineTree',
    method: 'post'
  })
}

export function describeProductLinePage(page, size, args) {
  return request({
    url: 'api/cmdb/productLine/describeProductLinePage',
    method: 'post',
    data: {
      page: page,
      size: size,
      args: args
    }
  })
}

export function createProductLine(args) {
  return request({
    url: 'api/cmdb/productLine/createProductLine',
    method: 'post',
    data: args
  })
}

export function deleteProductLine(args) {
  return request({
    url: 'api/cmdb/productLine/deleteProductLine',
    method: 'post',
    data: args
  })
}

export function updateProductLine(args) {
  return request({
    url: 'api/cmdb/productLine/updateProductLine',
    method: 'post',
    data: args
  })
}
