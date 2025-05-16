// 适配 Nginx 反向代理
const baseUrl = process.env.VUE_APP_BASE_API === '/' ? '' : process.env.VUE_APP_BASE_API
const api = {
  state: {
    // 图片上传
    imagesUploadApi: baseUrl + '/api/localStorage/uploadPicture',
    // 修改头像
    updateAvatarApi: baseUrl + '/api/users/modifyUserAvatar',
    // 上传文件到七牛云
    qiNiuUploadApi: baseUrl + '/api/qiNiuContent',
    // Sql 监控
    sqlApi: baseUrl + '/druid/index.html',
    // websocket
    websocketApi: baseUrl + '/webSocket/{sid}',
    // swagger
    swaggerApi: baseUrl + '/doc.html',
    // 文件上传
    fileUploadApi: baseUrl + '/api/localStorage/uploadFile',
    // baseUrl，
    baseApi: baseUrl
  }
}

export default api
