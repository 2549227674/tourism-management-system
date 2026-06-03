# 旅游管理系统

旅游管理系统是软件工程课程设计项目，采用前后端分离方式实现，面向普通用户和管理员提供旅游景点、旅游线路、订单预订、评论收藏、公告资讯和数据统计等功能。

## 1. 项目功能

### 用户端

- 用户注册、登录、退出；
- 个人信息管理；
- 景点列表和详情浏览；
- 旅游线路列表和详情浏览；
- 线路预订和订单取消；
- 收藏景点或线路；
- 提交评论；
- 查看公告资讯。

### 管理员端

- 管理员登录；
- 景点信息管理；
- 旅游线路管理；
- 订单查看和处理；
- 评论审核；
- 公告发布和下架；
- 景点数量、线路数量、订单数量和订单金额统计。

## 2. 技术栈

| 层次 | 技术 |
|---|---|
| 后端 | Java 17、Spring Boot 3、MyBatis-Plus、Spring Security/JWT |
| 数据库 | MySQL 8.0 |
| 前端 | Vue 3、Vite、Element Plus、Axios、Pinia |
| 文档 | Markdown |

## 3. 项目目录

```text
tourism-management-system/
├── backend/                  # 后端源程序
├── frontend/                 # 前端源程序
├── database/                 # 数据库脚本
├── docs/                     # 文档
│   ├── deliverables/          # 最终交付文档
│   └── dev/                   # 开发指导文档
├── CLAUDE.md                 # Claude Code 开发约束
└── README.md                 # 项目说明
```

## 4. 快速启动

### 4.1 初始化数据库

```sql
CREATE DATABASE tourism_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行脚本：

```bash
mysql -u tourism -p tourism_management < database/schema.sql
mysql -u tourism -p tourism_management < database/data.sql
```

> 默认数据库连接账号为 `tourism`/`tourism123`，对应 `backend/src/main/resources/application.yml` 中的配置。如本地 MySQL 账号不同，请同步修改该配置文件。

### 4.2 启动后端

```bash
cd backend
mvn clean test
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:8080/api
```

### 4.3 启动前端

```bash
cd frontend
npm install
npm run build
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

## 5. 默认账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | admin | admin123 |
| 普通用户 | user01 | user123 |
| 普通用户 | user02 | user123 |

## 6. 文档说明

### 6.1 最终交付文档

| 文件 | 说明 |
|---|---|
| `docs/deliverables/01-旅游管理系统可行性研究报告.md` | 立项和经济可行性分析。 |
| `docs/deliverables/02-旅游管理系统需求分析规格说明书.md` | 功能、性能、数据和运行环境需求。 |
| `docs/deliverables/03-旅游管理系统软件设计规格说明书.md` | 架构、模块、数据库、接口和页面设计。 |
| `docs/deliverables/04-旅游管理系统项目总结报告.md` | 项目完成后的开发结果和经验总结。 |
| `docs/deliverables/05-源程序说明与运行指南.md` | 源程序结构和运行步骤。 |

### 6.2 开发指导文档

开发前请阅读：

1. `docs/dev/00-项目总体说明.md`；
2. `docs/dev/01-技术栈与开发约束.md`；
3. `docs/dev/03-数据库设计说明.md`；
4. `docs/dev/04-后端接口设计说明.md`；
5. `docs/dev/06-权限与业务规则说明.md`；
6. `CLAUDE.md`。

## 7. 演示流程

1. 管理员登录后台；
2. 新增景点和旅游线路；
3. 普通用户注册并登录；
4. 普通用户浏览线路并提交预订；
5. 管理员确认订单；
6. 普通用户查看订单状态；
7. 普通用户提交评论；
8. 管理员审核评论；
9. 管理员查看统计数据。

## 8. 开发约束

1. 不随意更换技术栈；
2. 不扩大功能范围到支付、地图、短信等复杂模块；
3. 修改代码后同步更新相关文档；
4. 保证最终交付文档与源程序一致；
5. 管理员权限必须由后端校验；
6. 订单金额和线路名额必须由后端保证正确。

## 9. 测试状态

| 测试项 | 结果 |
|---|---|
| 后端 `mvn clean test` | 通过，BUILD SUCCESS |
| 前端 `npm run build` | 构建成功 |
| TC-FE-01 ~ TC-FE-100（前端功能测试） | 全部通过 |
| 运行指南验收检查表（12 项） | 全部通过 |

## 10. 课程交付清单

- [x] 可行性研究报告；
- [x] 需求分析规格说明书；
- [x] 软件设计规格说明书；
- [x] 项目总结报告；
- [x] 可运行软件源程序；
- [x] 数据库脚本；
- [x] 源程序说明与运行指南；
- [x] 测试用例与验收记录。

## 11. 许可说明

本项目仅用于软件工程课程设计学习和演示，不用于商业运营。演示数据均应使用虚构数据或经过授权的公开信息。
