import request from '@/utils/request'

export function kickOutUser(keys) {
  return request({
    url: 'auth/online/kickOutUser',
    method: 'post',
    data: keys
  })
}
