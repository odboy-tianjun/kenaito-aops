import request from '@/utils/request'

export function describeMenuListByPid(pid) {
  return request({
    url: 'api/menus/describeMenuListByPid?pid=' + pid,
    method: 'post'
  })
}

export function getMenus(params) {
  return request({
    url: 'api/menus',
    method: 'get',
    params
  })
}

export function describeMenuSuperior(ids) {
  const data = Array.isArray(ids) ? ids : [ids]
  return request({
    url: 'api/menus/describeMenuSuperior',
    method: 'post',
    data
  })
}

export function describeChildMenuSet(id) {
  return request({
    url: 'api/menus/describeChildMenuSet?id=' + id,
    method: 'post'
  })
}

export function buildMenus() {
  return request({
    url: 'api/menus/buildMenus',
    method: 'post'
  })
}

export function add(data) {
  return request({
    url: 'api/menus/saveMenu',
    method: 'post',
    data
  })
}

export function del(ids) {
  return request({
    url: 'api/menus/removeMenuByIds',
    method: 'post',
    data: ids
  })
}

export function edit(data) {
  return request({
    url: 'api/menus/modifyMenuById',
    method: 'post',
    data
  })
}

export default { add, edit, del, describeMenuListByPid, describeMenuSuperior, getMenus, describeChildMenuSet }
