# aops-front

## 前端模板

初始模板基于： [https://github.com/PanJiaChen/vue-element-admin](https://github.com/PanJiaChen/vue-element-admin)

模板文档： [https://panjiachen.github.io/vue-element-admin-site/zh/guide/](https://panjiachen.github.io/vue-element-admin-site/zh/guide/)

## 模块组成

- aops
  - 项目管理
    - appManage(新建、查询、配置)
  - 持续集成、持续部署
    - appCenter 应用中心
      - 概览
      - 部署记录(发布记录，回滚等)
      - 环境管理(应用环境内容器数量、健康状态，升降配，进容器等操作)
      - 链路监控(skywalking)
  - 监控、日志
    - 日志(Grafana Loki)
    - 性能监控(skywalking)
  - 知识库(待定)
  - 对外提供微服务(待定)

## 快速开始

### 环境要求

**推荐 node 版本：12-16 LTS**

### 安装依赖

``` bash
# 安装依赖（依赖python）
npm install --registry https://registry.npmmirror.com
```

### 启动服务

```bash
# 启动服务 localhost:8013
npm run dev

# (或) 启动服务 localhost:8013
IDEA -> Current File -> Edit Configurations... -> Add New Configuration -> npm -> Script选dev -> Apply -> Ok

# 构建生产环境
npm run build:prod
```

## 常见问题

1、linux 系统在安装依赖的时候会出现 node-sass 无法安装的问题

解决方案：

```
1. 单独安装：npm install --unsafe-perm node-sass 
2. 直接使用：npm install --unsafe-perm
```

2、加速node-sass安装

https://www.ydyno.com/archives/1219.html

## 特别鸣谢

- 感谢 [JetBrains](https://www.jetbrains.com/) 提供的非商业开源软件开发授权

- 感谢 [PanJiaChen](https://github.com/PanJiaChen/vue-element-admin) 大佬提供的前端模板

- 感谢 [Moxun](https://github.com/moxun1639) 大佬提供的前端 Crud 通用组件

- 感谢 [zhy6599](https://gitee.com/zhy6599) 大佬提供的后端运维管理相关功能

- 感谢 [j.yao.SUSE](https://github.com/everhopingandwaiting) 大佬提供的匿名接口与Redis限流等功能
