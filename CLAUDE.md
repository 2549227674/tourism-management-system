# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

本文件用于约束 Claude Code 在"旅游管理系统"项目中的开发行为。所有自动生成或修改代码的操作必须遵守本文件。

## 1. 项目简介

项目名称：旅游管理系统
项目性质：软件工程课程设计
目标：完成一个小型、可运行、文档完整的 Web 系统。

**当前状态：** 后端骨架已搭建（用户注册、登录、JWT 认证），其余业务模块待开发。开发按 `docs/dev/07-项目开发计划与任务分工.md` 计划执行。

系统功能范围：

- 用户注册、登录、个人信息管理；
- 管理员登录与权限控制；
- 景点信息管理；
- 旅游线路管理；
- 订单预订、取消和管理员处理；
- 评论与收藏管理；
- 公告资讯管理；
- 数据统计。

## 2. 构建与运行命令

### 数据库初始化

```bash
mysql -u root -p -e "CREATE DATABASE tourism_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p tourism_management < database/schema.sql
mysql -u root -p tourism_management < database/data.sql
```

### 启动后端（需 JDK 17 + Maven）

```bash
cd backend
mvn clean install
mvn spring-boot:run
# API 地址：http://localhost:8080/api
```

### 启动前端（需 Node.js）

```bash
cd frontend
npm install
npm run dev
# 页面地址：http://localhost:5173
```

### 默认账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | admin | admin123 |
| 普通用户 | user01 | user123 |

最终提交前请以 `database/data.sql` 中的真实账号为准。

## 3. 必须遵守的技术栈

不得随意更换技术栈。

| 层次 | 技术 |
|---|---|
| 后端 | Java 17 + Spring Boot 3.x |
| 后端数据库访问 | MyBatis-Plus |
| 后端认证 | Spring Security + JWT |
| 数据库 | MySQL 8.0（utf8mb4） |
| 前端 | Vue 3 + Vite |
| UI | Element Plus |
| 前端请求 | Axios |
| 状态管理 | Pinia |
| 文档 | Markdown |

禁止将项目改成以下架构，除非用户明确要求并同步全部文档：

- 微服务架构；
- 纯静态页面；
- 无数据库版本；
- 其他后端语言或数据库；
- 引入支付、地图、短信、推荐算法等超范围功能。

## 4. 项目架构

```text
tourism-management-system/
├── backend/                     # Spring Boot 后端（Maven 项目）
│   └── src/main/java/com/tourism/
│       ├── controller/          # REST 控制器（/api/**、/api/admin/**）
│       ├── service/             # 业务逻辑层
│       ├── mapper/              # MyBatis-Plus Mapper 接口
│       ├── entity/              # 数据库实体类
│       ├── dto/                 # 请求/响应 DTO
│       ├── config/              # Spring Security、JWT、CORS 等配置
│       └── common/              # 统一响应格式、常量、异常处理
├── frontend/                    # Vue 3 前端（Vite 项目）
│   └── src/
│       ├── api/                 # Axios 请求模块
│       ├── views/               # 页面组件（用户端 + 管理端）
│       ├── components/          # 公共组件
│       ├── router/              # Vue Router（含路由守卫）
│       ├── stores/              # Pinia 状态管理（认证等）
│       └── utils/               # Axios 实例、工具函数
├── database/
│   ├── schema.sql               # DDL（8 张表）
│   └── data.sql                 # 初始化数据（管理员 + 测试用户）
└── docs/                        # 课程交付文档 + 开发指导文档
```

### 核心设计决策

- 所有接口以 `/api` 开头，管理员接口以 `/api/admin` 开头；
- 统一 JSON 响应格式；
- 密码使用 BCrypt 加密存储，禁止明文；
- 订单金额由后端计算，线路名额在订单创建/取消/驳回时由后端维护；
- JWT token 存储于 Pinia + localStorage，Axios 拦截器自动附加；
- 路由守卫控制页面访问权限，管理员菜单仅对 ADMIN 角色显示。

## 5. 开发顺序

请按模块逐步开发，不要一次性大范围生成无关代码。

1. 项目骨架和运行配置；
2. 数据库建表和初始化数据；
3. 用户注册、登录和权限控制；
4. 景点信息管理；
5. 旅游线路管理；
6. 订单预订、取消和管理员处理；
7. 评论与收藏；
8. 公告资讯；
9. 数据统计；
10. 测试、修复和文档同步。

## 6. 目录约束

不要删除 `docs/` 目录，不要随意移动交付文档。

## 7. 业务规则约束

### 7.1 角色

- USER：普通用户；
- ADMIN：管理员。

### 7.2 线路状态

- DRAFT：草稿；
- OPEN：开放预订；
- FULL：名额已满；
- CLOSED：已关闭。

只有 OPEN 状态线路允许预订。

### 7.3 订单状态

- PENDING：待处理；
- CONFIRMED：已确认；
- CANCELLED：已取消；
- REJECTED：已驳回；
- COMPLETED：已完成。

取消或驳回订单时必须释放线路名额。

### 7.4 评论状态

- PENDING：待审核；
- APPROVED：已通过；
- REJECTED：已驳回。

只有 APPROVED 评论可在用户端展示。

## 8. 编码约束

### 8.1 数据库

1. 表名和字段名使用小写下划线；
2. 主键统一使用 `id`；
3. 时间字段统一使用 `created_at`、`updated_at`；
4. 状态字段统一使用 `status`；
5. 用户名、订单号、收藏关系等必须加唯一约束；
6. 初始化数据必须包含管理员账号和普通用户账号。

### 8.2 后端

1. 后端接口统一以 `/api` 开头；
2. 管理员接口统一以 `/api/admin` 开头；
3. 所有返回值使用统一响应格式；
4. 密码必须加密存储，禁止明文保存；
5. 管理员接口必须做后端权限校验；
6. 订单金额必须由后端计算；
7. 订单创建、取消、驳回必须正确维护线路名额。

### 8.3 前端

1. 通过 Axios 统一封装请求；
2. token 由 Pinia 或本地存储统一管理；
3. 路由守卫负责基础页面权限控制；
4. 管理员菜单只对 ADMIN 显示；
5. 表单必须做必要校验；
6. 页面字段名称应与接口文档一致。

## 9. 文档同步要求

每次修改代码后，必须检查是否需要更新相关文档。

| 修改内容 | 必须同步文档 |
|---|---|
| 新增或修改接口 | `docs/dev/04-后端接口设计说明.md` |
| 修改数据库表或字段 | `docs/dev/03-数据库设计说明.md` |
| 修改页面路由或交互 | `docs/dev/05-前端页面与交互说明.md` |
| 修改权限或业务规则 | `docs/dev/06-权限与业务规则说明.md` |
| 修改运行方式 | `README.md`、`docs/deliverables/05-源程序说明与运行指南.md` |
| 修改功能范围 | 需求规格说明书、软件设计规格说明书 |
| 修复关键缺陷 | `docs/dev/08-测试用例与验收标准.md`、项目总结报告 |

最终提交前必须保证：

1. 交付文档写到的功能，源代码中能够运行或明确标注为未完成；
2. 源代码实现的核心功能，文档中有说明；
3. 接口路径、数据库字段、状态值、运行命令一致；
4. 文档中不得残留"图书管理系统"等与本项目无关内容。

## 10. 每次任务完成后的输出要求

Claude Code 每次完成任务后，应给出：

1. 本次修改的文件清单；
2. 完成的功能点；
3. 如何运行或测试；
4. 是否更新了文档；
5. 仍需人工确认的问题。

## 11. 禁止事项

- 禁止删除或忽略课程交付文档；
- 禁止将功能扩展到难以完成的范围；
- 禁止绕过后端权限校验；
- 禁止硬编码真实密码、密钥或个人隐私信息；
- 禁止修改技术栈后不更新文档；
- 禁止生成与旅游管理系统无关的业务代码；
- 禁止只更新源代码而不更新相关文档。

## 12. 最终交付前检查

1. 数据库脚本能执行；
2. 后端能启动；
3. 前端能启动；
4. 默认账号能登录；
5. 用户端核心流程能走通；
6. 管理员端核心流程能走通；
7. 测试用例已执行并记录；
8. README 和运行指南真实有效；
9. 项目总结报告已根据实际结果更新；
10. 所有交付文档和源代码一致。
