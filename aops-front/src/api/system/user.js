import request from '@/utils/request'
import { encrypt } from '@/utils/rsaEncrypt'

export function add(data) {
  return request({
    url: 'api/users/saveUser',
    method: 'post',
    data
  })
}

export function del(ids) {
  return request({
    url: 'api/users/removeUserByIds',
    method: 'post',
    data: ids
  })
}

export function resetPwd(ids) {
  return request({
    url: 'api/users/resetUserPasswordByIds',
    method: 'post',
    data: ids
  })
}

export function edit(data) {
  return request({
    url: 'api/users/modifyUserById',
    method: 'post',
    data
  })
}

export function editUser(data) {
  return request({
    url: 'api/users/modifyUserCenterInfoById',
    method: 'post',
    data
  })
}

export function updatePass(user) {
  const data = {
    oldPass: encrypt(user.oldPass),
    newPass: encrypt(user.newPass)
  }
  return request({
    url: 'api/users/modifyUserPasswordByUsername',
    method: 'post',
    data
  })
}

export function updateEmail(form) {
  const data = {
    password: encrypt(form.pass),
    email: form.email
  }
  return request({
    url: 'api/users/modifyUserEmailByUsername/' + form.code,
    method: 'post',
    data
  })
}

export function queryUserMetaPage(query) {
  return request({
    url: 'api/users/describeUserMetadataOptions',
    method: 'post',
    data: {
      page: 1,
      pageSize: 50,
      blurry: query
    }
  })
}

export default { add, edit, del, resetPwd, queryUserMetaPage }

