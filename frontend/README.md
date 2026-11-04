# 拾豆咖啡 · 前端工程

咖啡电商与订阅平台的 Vue3 前端，技术栈与接口契约严格对齐：

- **Vue 3.5** + `<script setup lang="ts">`
- **Vite 7** + **TypeScript 5.9**
- **Pinia 3**（用户 / 购物车 / 应用 / 订阅 / 通知）
- **Vue Router 4**（登录拦截 + 管理员路由）
- **Element Plus 2**（中文语言包、全量图标）
- **Axios**（统一实例、Token 注入、错误与 401 处理）

后端工程位于同级 `backend/`，接口文档见 `docs/05-API设计文档.md`，页面与工程结构见 `docs/06-前端设计.md`。

## 快速开始

要求 Node.js >= 20.19（Vite 7 要求），推荐使用当前 LTS。

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173
```

> 如果本机 npm 缓存权限异常（EPERM），可改用工程内缓存：
> `npm install --cache .npm-cache`（`.npm-cache` 已加入 `.gitignore`）。

开发环境通过 Vite 将 `/api` 代理到 `http://localhost:8080`，无需额外配置跨域。
如果后端临时跑在其他端口，可覆盖代理目标：

```bash
VITE_PROXY_TARGET=http://localhost:8081 npm run dev
```

```bash
npm run type-check # vue-tsc 类型检查
npm run build      # 类型检查 + 生产构建，产物在 dist/
npm run preview    # 预览生产构建
```

> 本轮新增了通知表迁移 `V8__init_notification_table.sql`，启动后端前请确认 Flyway 已执行；
> 同时需要重启后端以加载分页插件与新接口。

## 环境变量

| 文件 | 变量 | 说明 |
| :--- | :--- | :--- |
| `.env.development` | `VITE_API_BASE_URL=/api/v1` | 开发环境走 Vite 代理 |
| `.env.production` | `VITE_API_BASE_URL=/api/v1` | 生产环境由 Nginx 反向代理 `/api` |
| 两者 | `VITE_APP_TITLE` | 浏览器标题前缀 |

## 目录结构

```text
frontend/
├── public/
├── src/
│   ├── api/            # 按模块拆分：auth/user/product/cart/order/payment/subscription/admin/inventory/notification
│   ├── assets/         # 样式、品牌插画、占位图
│   ├── components/     # common / product / order / subscription / user
│   ├── composables/    # useAuth / useCart / usePagination / useLoading
│   ├── layouts/        # DefaultLayout / AdminLayout / BlankLayout
│   ├── router/         # routes / guard / index
│   ├── stores/         # user / cart / app / subscription / notification
│   ├── types/          # 与 API 文档字段一一对应的类型
│   ├── utils/          # request / auth / format / constants
│   ├── views/          # auth / home / product / cart / order / subscription / user / notification / admin / error
│   ├── App.vue
│   └── main.ts
├── .env.development
├── .env.production
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 页面与路由

| 路由 | 页面 | 鉴权 |
| :--- | :--- | :--- |
| `/` | 首页 | 否 |
| `/login`、`/register` | 登录、注册 | 否 |
| `/products`、`/products/:id` | 商品列表、商品详情 | 否 |
| `/cart` | 购物车 | 是 |
| `/order/confirm` | 订单确认 | 是 |
| `/orders`、`/orders/:orderNo` | 我的订单、订单详情 | 是 |
| `/subscriptions/plans` | 订阅计划 | 否 |
| `/subscriptions/my` | 我的订阅 | 是 |
| `/user/profile`、`/user/address`、`/user/preference` | 个人中心、地址、偏好 | 是 |
| `/notifications` | 消息通知（订单/订阅） | 是 |
| `/admin` | 数据看板 | 管理员 |
| `/admin/products` | 商品管理（商品/SKU 增删改查、上下架） | 管理员 |
| `/admin/inventory` | 库存管理（查看、调整、流水） | 管理员 |
| `/admin/orders` | 订单管理（查看、发货） | 管理员 |
| `/admin/subscriptions` | 订阅管理（查看、暂停、取消） | 管理员 |

## 关键约定

### 请求与错误处理

- 所有请求从 `@/utils/request` 发出，`api/` 负责路径与类型，组件不直接调用 axios。
- 响应拦截器统一解包 `{ code, message, data, traceId }`，`code === 0` 时直接返回 `data`。
- 业务码或 HTTP 401 会清理本地凭证并跳转 `/login?redirect=...`；403 提示无权限；请求可通过 `silent: true` 关闭全局错误提示。
- Token 与登录用户信息保存在 `localStorage`，刷新页面后保持登录态。

### 角色与权限

- 后端登录响应 `userInfo.role` 为 `USER` / `ADMIN`。
- 路由 `meta.requiresAuth`、`meta.requiresAdmin` 由 `router/guard.ts` 统一校验。
- `/users/me` 不返回角色字段，前端在获取资料时保留登录时写入的角色。
- 管理员账号需在数据库中设置 `user.role = 'ADMIN'`。

### 支付

- 支付页会调用 `POST /payments/{orderNo}/pay` 创建支付单。
- 当前项目是学习/演示环境，收银台提供「模拟支付成功」按钮，调用后端约定的 `POST /payments/callback`（`sign = SUCCESS_SIGN`）完成闭环。
- 真实生产环境应由第三方服务端异步回调，前端不应调用回调接口。

### 通知

- 顶部通知中心与 `/notifications` 页面共用 Pinia 通知 Store；在列表页标记已读会同步角标。
- 订单创建、支付、发货、取消以及订阅创建、暂停、跳过、取消、生成订单都会写入站内通知。
- 角标未读数基于最近 10 条通知计算；完整列表在 `/notifications` 分页查看。

## 本轮补齐的后端能力

本轮不仅完善前端，还补齐了前端依赖的后端缺口，保证功能可真实使用：

| 能力 | 后端实现 |
| :--- | :--- |
| 分页 | 注册 `PaginationInnerInterceptor`，修复 `selectPage` 无 LIMIT、`total=0` 问题 |
| 订单快照 | 下单时写入真实商品名称，修复订单项固定为「咖啡商品」的问题 |
| 后台商品列表 | `GET /admin/products`，支持关键词、烘焙度、上下架状态筛选，含下架商品 |
| 后台商品详情 | `GET /admin/products/{id}`，含下架商品与 SKU |
| 后台商品编辑 | `PUT /admin/products/{id}` 支持同步更新/新增 SKU；库存变化写入库存流水 |
| 后台商品删除 | `DELETE /admin/products/{id}`，逻辑删除商品与 SKU，订单快照不受影响 |
| 后台订阅管理 | `GET /admin/subscriptions`，新增 `PUT /admin/subscriptions/{id}/pause|cancel` |
| 库存管理 | `GET /inventory/skus` 分页列表；库存调整与流水复用已有接口 |
| 站内通知 | 新增 `notification` 表与 `GET /notifications`、`PUT /notifications/{id}/read`；订单/订阅事件自动写入 |
| 订阅创建响应 | 增加 `subscriptionId` 字段，兼容 API 文档 |

## 剩余说明

- 后台商品编辑采用「已有 SKU 更新 + 新增 SKU」的安全策略，暂不支持删除已有 SKU，避免破坏购物车与订阅引用。
- 优惠券、积分、退款不在当前 API 文档范围内，未实现。
- 创建订阅时前端按文档提交烘焙度/风味/处理法，当前后端 DTO 仅持久化 `planId`；后端扩展后可无缝使用。
- 通知角标按最近 10 条计算，若通知量很大需要后端提供未读计数接口。

## 验证情况

- `npm run type-check` 通过（vue-tsc，严格模式、未使用变量检查）。
- `npm run build` 通过，页面按路由懒加载。
- 使用真实后端 + 无头 Chrome 走通：
  - 登录、商品、购物车、订单创建/支付/发货、订阅创建/暂停/取消
  - 后台商品列表、SKU 编辑、商品创建/删除
  - 库存列表、库存调整、库存流水
  - 通知生成、通知列表、标记已读与角标同步
  - 桌面端与移动端页面渲染，无浏览器运行时错误
