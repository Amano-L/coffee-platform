# 05-API设计文档

| 项目名称 | 咖啡电商与订阅平台            |
| -------- | ----------------------------- |
| 文档版本 | v1.2                          |
| 编制人   | 项目经理                      |
| 编制日期 | 2026-10-14                    |
| 状态     | 已评审                        |
| 适用范围 | 项目全体成员、AI 辅助开发流程 |
| Base URL | `/api/v1`                     |
| 认证方式 | JWT（Bearer Token）           |

## 1. 全局约定

### 1.1 请求规范
- 所有接口以 `/api/v1` 为前缀。
- 请求体统一使用 `application/json`。
- 时间格式：`yyyy-MM-dd HH:mm:ss`。
- 金额格式：`DECIMAL(10,2)`，单位元。

### 1.2 认证方式
- 除注册、登录、商品列表、商品详情、订阅计划外，其余接口均需登录。
- 请求头：`Authorization: Bearer <token>`
- Token 失效或未登录返回 `401`。

### 1.3 统一响应结构
```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "a1b2c3d4"
}
```

| 字段    | 类型   | 说明                  |
| :------ | :----- | :-------------------- |
| code    | int    | 0 成功，非 0 业务异常 |
| message | string | 提示信息              |
| data    | object | 业务数据，可为空      |
| traceId | string | 请求链路追踪 ID       |

### 1.4 统一分页结构

json

```
{
  "records": [],
  "total": 100,
  "current": 1,
  "size": 10
}
```



### 1.5 错误码规范

| 错误码 | 说明                |
| :----- | :------------------ |
| 0      | 成功                |
| 400    | 请求参数错误        |
| 401    | 未登录或 Token 失效 |
| 403    | 无权限访问          |
| 404    | 资源不存在          |
| 4001   | 库存不足            |
| 4002   | 订单状态异常        |
| 4003   | 支付回调重复        |
| 4004   | 订阅状态异常        |
| 4005   | 购物车为空          |
| 4006   | 收货地址不存在      |
| 4007   | 用户名已存在        |
| 4008   | 手机号已存在        |
| 4009   | 用户名或密码错误    |
| 4010   | 账号已被禁用        |
| 5000   | 系统异常            |

## 2. 认证模块

### 2.1 注册

| 项目 | 内容             |
| :--- | :--------------- |
| 方法 | POST             |
| 路径 | `/auth/register` |
| 鉴权 | 否               |
| 说明 | 用户注册         |

**请求体**

json

```
{
  "username": "coffee_lover",
  "password": "123456",
  "phone": "13800138000",
  "email": "coffee@example.com"
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "coffee_lover"
  },
  "traceId": "a1b2c3d4"
}
```



### 2.2 登录

| 项目 | 内容                     |
| :--- | :----------------------- |
| 方法 | POST                     |
| 路径 | `/auth/login`            |
| 鉴权 | 否                       |
| 说明 | 用户登录，返回 JWT Token |

**请求体**

json

```
{
  "username": "coffee_lover",
  "password": "123456"
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": 1,
      "username": "coffee_lover",
      "nickname": "咖啡爱好者",
      "avatarUrl": "https://xxx.com/avatar.jpg"
    }
  },
  "traceId": "a1b2c3d4"
}
```



### 2.3 登出

| 项目 | 内容                  |
| :--- | :-------------------- |
| 方法 | POST                  |
| 路径 | `/auth/logout`        |
| 鉴权 | 是                    |
| 说明 | 删除 Redis 中的 Token |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": null,
  "traceId": "a1b2c3d4"
}
```



## 3. 用户模块

### 3.1 获取个人信息

| 项目 | 内容                 |
| :--- | :------------------- |
| 方法 | GET                  |
| 路径 | `/users/me`          |
| 鉴权 | 是                   |
| 说明 | 获取当前登录用户信息 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "username": "coffee_lover",
    "nickname": "咖啡爱好者",
    "phone": "13800138000",
    "email": "coffee@example.com",
    "avatarUrl": "https://xxx.com/avatar.jpg",
    "status": 1
  },
  "traceId": "a1b2c3d4"
}
```



### 3.2 修改个人信息

| 项目 | 内容                   |
| :--- | :--------------------- |
| 方法 | PUT                    |
| 路径 | `/users/me`            |
| 鉴权 | 是                     |
| 说明 | 修改昵称、头像、手机号 |

**请求体**

json

```
{
  "nickname": "咖啡大师",
  "avatarUrl": "https://xxx.com/new-avatar.jpg",
  "phone": "13900139000"
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "nickname": "咖啡大师",
    "avatarUrl": "https://xxx.com/new-avatar.jpg",
    "phone": "13900139000"
  },
  "traceId": "a1b2c3d4"
}
```



### 3.3 收货地址列表

| 项目 | 内容                 |
| :--- | :------------------- |
| 方法 | GET                  |
| 路径 | `/users/addresses`   |
| 鉴权 | 是                   |
| 说明 | 查询当前用户收货地址 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "receiverName": "张三",
      "phone": "13800138000",
      "province": "浙江省",
      "city": "杭州市",
      "district": "西湖区",
      "detailAddress": "文三路 100 号",
      "isDefault": 1
    }
  ],
  "traceId": "a1b2c3d4"
}
```



### 3.4 新增收货地址

| 项目 | 内容               |
| :--- | :----------------- |
| 方法 | POST               |
| 路径 | `/users/addresses` |
| 鉴权 | 是                 |
| 说明 | 新增收货地址       |

**请求体**

json

```
{
  "receiverName": "李四",
  "phone": "13900139000",
  "province": "上海市",
  "city": "上海市",
  "district": "浦东新区",
  "detailAddress": "世纪大道 1 号",
  "isDefault": 0
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 2,
    "receiverName": "李四",
    "phone": "13900139000",
    "province": "上海市",
    "city": "上海市",
    "district": "浦东新区",
    "detailAddress": "世纪大道 1 号",
    "isDefault": 0
  },
  "traceId": "a1b2c3d4"
}
```



### 3.5 修改收货地址

| 项目 | 内容                    |
| :--- | :---------------------- |
| 方法 | PUT                     |
| 路径 | `/users/addresses/{id}` |
| 鉴权 | 是                      |
| 说明 | 修改收货地址            |

**请求体**

json

```
{
  "receiverName": "李四",
  "phone": "13900139000",
  "province": "上海市",
  "city": "上海市",
  "district": "浦东新区",
  "detailAddress": "世纪大道 2 号",
  "isDefault": 1
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 2,
    "receiverName": "李四",
    "phone": "13900139000",
    "province": "上海市",
    "city": "上海市",
    "district": "浦东新区",
    "detailAddress": "世纪大道 2 号",
    "isDefault": 1
  },
  "traceId": "a1b2c3d4"
}
```



### 3.6 删除收货地址

| 项目 | 内容                    |
| :--- | :---------------------- |
| 方法 | DELETE                  |
| 路径 | `/users/addresses/{id}` |
| 鉴权 | 是                      |
| 说明 | 删除收货地址            |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": null,
  "traceId": "a1b2c3d4"
}
```



### 3.7 设置默认地址

| 项目 | 内容                            |
| :--- | :------------------------------ |
| 方法 | PUT                             |
| 路径 | `/users/addresses/{id}/default` |
| 鉴权 | 是                              |
| 说明 | 设置默认收货地址                |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 2,
    "isDefault": 1
  },
  "traceId": "a1b2c3d4"
}
```



### 3.8 获取咖啡偏好

| 项目 | 内容                 |
| :--- | :------------------- |
| 方法 | GET                  |
| 路径 | `/users/preference`  |
| 鉴权 | 是                   |
| 说明 | 获取当前用户咖啡偏好 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "roastLevel": "LIGHT",
    "flavorTags": ["花香", "柑橘"],
    "processMethod": "WASHED"
  },
  "traceId": "a1b2c3d4"
}
```



### 3.9 保存咖啡偏好

| 项目 | 内容                |
| :--- | :------------------ |
| 方法 | PUT                 |
| 路径 | `/users/preference` |
| 鉴权 | 是                  |
| 说明 | 保存或更新咖啡偏好  |

**请求体**

json

```
{
  "roastLevel": "MEDIUM",
  "flavorTags": ["坚果", "巧克力"],
  "processMethod": "NATURAL"
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "roastLevel": "MEDIUM",
    "flavorTags": ["坚果", "巧克力"],
    "processMethod": "NATURAL"
  },
  "traceId": "a1b2c3d4"
}
```



## 4. 商品模块

### 4.1 商品列表

| 项目 | 内容                   |
| :--- | :--------------------- |
| 方法 | GET                    |
| 路径 | `/products`            |
| 鉴权 | 否                     |
| 说明 | 分页查询商品，支持筛选 |

**Query 参数**

| 参数          | 类型   | 必填 | 说明                      |
| :------------ | :----- | :--- | :------------------------ |
| page          | int    | 否   | 页码，默认 1              |
| size          | int    | 否   | 每页条数，默认 10         |
| keyword       | string | 否   | 关键词，匹配商品名称      |
| roastLevel    | string | 否   | 烘焙度：LIGHT/MEDIUM/DARK |
| flavorTag     | string | 否   | 风味标签                  |
| processMethod | string | 否   | 处理法                    |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "埃塞俄比亚 耶加雪菲",
        "origin": "埃塞俄比亚",
        "roastLevel": "LIGHT",
        "flavorTags": ["花香", "柑橘"],
        "processMethod": "WASHED",
        "minPrice": 68.00,
        "coverUrl": "https://xxx.com/1.jpg"
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10
  },
  "traceId": "a1b2c3d4"
}
```



### 4.2 商品详情

| 项目 | 内容                      |
| :--- | :------------------------ |
| 方法 | GET                       |
| 路径 | `/products/{id}`          |
| 鉴权 | 否                        |
| 说明 | 查询商品详情，含 SKU 列表 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "name": "埃塞俄比亚 耶加雪菲",
    "origin": "埃塞俄比亚",
    "roastLevel": "LIGHT",
    "flavorTags": ["花香", "柑橘"],
    "processMethod": "WASHED",
    "description": "经典水洗耶加雪菲，花香柑橘调性。",
    "coverUrl": "https://xxx.com/1.jpg",
    "skus": [
      {
        "id": 1,
        "weight": "250g",
        "grindType": "WHOLE_BEAN",
        "price": 68.00,
        "stock": 100
      },
      {
        "id": 2,
        "weight": "500g",
        "grindType": "GROUND",
        "price": 128.00,
        "stock": 50
      }
    ]
  },
  "traceId": "a1b2c3d4"
}
```



## 5. 购物车模块

### 5.1 获取购物车

| 项目 | 内容               |
| :--- | :----------------- |
| 方法 | GET                |
| 路径 | `/cart`            |
| 鉴权 | 是                 |
| 说明 | 获取当前用户购物车 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "items": [
      {
        "id": 1,
        "skuId": 1,
        "productName": "埃塞俄比亚 耶加雪菲",
        "weight": "250g",
        "grindType": "WHOLE_BEAN",
        "price": 68.00,
        "quantity": 2,
        "checked": 1,
        "coverUrl": "https://xxx.com/1.jpg"
      }
    ],
    "totalAmount": 136.00
  },
  "traceId": "a1b2c3d4"
}
```



### 5.2 加入购物车

| 项目 | 内容             |
| :--- | :--------------- |
| 方法 | POST             |
| 路径 | `/cart/items`    |
| 鉴权 | 是               |
| 说明 | 添加商品到购物车 |

**请求体**

json

```
{
  "skuId": 1,
  "quantity": 2
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "cartItemId": 1,
    "skuId": 1,
    "quantity": 2
  },
  "traceId": "a1b2c3d4"
}
```



### 5.3 修改购物车项

| 项目 | 内容               |
| :--- | :----------------- |
| 方法 | PUT                |
| 路径 | `/cart/items/{id}` |
| 鉴权 | 是                 |
| 说明 | 修改数量或勾选状态 |

**请求体**

json

```
{
  "quantity": 3,
  "checked": 1
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "quantity": 3,
    "checked": 1
  },
  "traceId": "a1b2c3d4"
}
```



### 5.4 删除购物车项

| 项目 | 内容               |
| :--- | :----------------- |
| 方法 | DELETE             |
| 路径 | `/cart/items/{id}` |
| 鉴权 | 是                 |
| 说明 | 删除购物车项       |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": null,
  "traceId": "a1b2c3d4"
}
```



### 5.5 清空购物车

| 项目 | 内容               |
| :--- | :----------------- |
| 方法 | DELETE             |
| 路径 | `/cart`            |
| 鉴权 | 是                 |
| 说明 | 清空当前用户购物车 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": null,
  "traceId": "a1b2c3d4"
}
```



## 6. 订单模块

### 6.1 创建订单

| 项目 | 内容                 |
| :--- | :------------------- |
| 方法 | POST                 |
| 路径 | `/orders`            |
| 鉴权 | 是                   |
| 说明 | 结算购物车，创建订单 |

**请求体**

json

```
{
  "addressId": 1,
  "cartItemIds": [1, 2],
  "couponId": null
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "orderNo": "COF202610140001",
    "totalAmount": 136.00,
    "payAmount": 136.00,
    "status": "待支付"
  },
  "traceId": "a1b2c3d4"
}
```



### 6.2 订单列表

| 项目 | 内容                 |
| :--- | :------------------- |
| 方法 | GET                  |
| 路径 | `/orders`            |
| 鉴权 | 是                   |
| 说明 | 分页查询当前用户订单 |

**Query 参数**

| 参数   | 类型   | 必填 | 说明     |
| :----- | :----- | :--- | :------- |
| page   | int    | 否   | 页码     |
| size   | int    | 否   | 每页条数 |
| status | string | 否   | 订单状态 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "orderNo": "COF202610140001",
        "totalAmount": 136.00,
        "payAmount": 136.00,
        "status": "待支付",
        "createTime": "2026-10-14 10:00:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  },
  "traceId": "a1b2c3d4"
}
```



### 6.3 订单详情

| 项目 | 内容                |
| :--- | :------------------ |
| 方法 | GET                 |
| 路径 | `/orders/{orderNo}` |
| 鉴权 | 是                  |
| 说明 | 查询订单详情        |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "orderNo": "COF202610140001",
    "totalAmount": 136.00,
    "payAmount": 136.00,
    "status": "待支付",
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "receiverAddress": "浙江省杭州市西湖区文三路 100 号",
    "items": [
      {
        "skuId": 1,
        "productName": "埃塞俄比亚 耶加雪菲",
        "price": 68.00,
        "quantity": 2
      }
    ],
    "createTime": "2026-10-14 10:00:00"
  },
  "traceId": "a1b2c3d4"
}
```



### 6.4 取消订单

| 项目 | 内容                         |
| :--- | :--------------------------- |
| 方法 | POST                         |
| 路径 | `/orders/{orderNo}/cancel`   |
| 鉴权 | 是                           |
| 说明 | 取消待支付订单，释放预占库存 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": null,
  "traceId": "a1b2c3d4"
}
```



## 7. 支付模块

### 7.1 发起支付

| 项目 | 内容                      |
| :--- | :------------------------ |
| 方法 | POST                      |
| 路径 | `/payments/{orderNo}/pay` |
| 鉴权 | 是                        |
| 说明 | 发起模拟支付，生成支付单  |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "payNo": "PAY202610140001",
    "orderNo": "COF202610140001",
    "amount": 136.00,
    "status": "待支付"
  },
  "traceId": "a1b2c3d4"
}
```



### 7.2 支付回调

| 项目 | 内容                     |
| :--- | :----------------------- |
| 方法 | POST                     |
| 路径 | `/payments/callback`     |
| 鉴权 | 验签                     |
| 说明 | 模拟支付回调，需幂等处理 |

**请求体**

json

```
{
  "payNo": "PAY202610140001",
  "orderNo": "COF202610140001",
  "amount": 136.00,
  "status": "SUCCESS",
  "sign": "xxx"
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": null,
  "traceId": "a1b2c3d4"
}
```



### 7.3 查询支付状态

| 项目 | 内容                |
| :--- | :------------------ |
| 方法 | GET                 |
| 路径 | `/payments/{payNo}` |
| 鉴权 | 是                  |
| 说明 | 查询支付单状态      |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "payNo": "PAY202610140001",
    "orderNo": "COF202610140001",
    "amount": 136.00,
    "status": "已支付",
    "payTime": "2026-10-14 10:05:00"
  },
  "traceId": "a1b2c3d4"
}
```



## 8. 库存模块（内部/管理）

### 8.1 查询 SKU 库存

| 项目 | 内容                      |
| :--- | :------------------------ |
| 方法 | GET                       |
| 路径 | `/inventory/skus/{skuId}` |
| 鉴权 | 是（管理员）              |
| 说明 | 查询 SKU 库存             |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "skuId": 1,
    "productName": "埃塞俄比亚 耶加雪菲",
    "weight": "250g",
    "stock": 100,
    "version": 0
  },
  "traceId": "a1b2c3d4"
}
```



### 8.2 调整库存

| 项目 | 内容                      |
| :--- | :------------------------ |
| 方法 | PUT                       |
| 路径 | `/inventory/skus/{skuId}` |
| 鉴权 | 是（管理员）              |
| 说明 | 调整库存数量              |

**请求体**

json

```
{
  "changeNum": 100,
  "reason": "采购入库"
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "skuId": 1,
    "stock": 200,
    "version": 1
  },
  "traceId": "a1b2c3d4"
}
```



### 8.3 库存流水

| 项目 | 内容              |
| :--- | :---------------- |
| 方法 | GET               |
| 路径 | `/inventory/logs` |
| 鉴权 | 是（管理员）      |
| 说明 | 分页查询库存流水  |

**Query 参数**

| 参数    | 类型   | 必填 | 说明     |
| :------ | :----- | :--- | :------- |
| skuId   | long   | 否   | SKU ID   |
| orderNo | string | 否   | 订单号   |
| page    | int    | 否   | 页码     |
| size    | int    | 否   | 每页条数 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "skuId": 1,
        "orderNo": "COF202610140001",
        "changeNum": -2,
        "type": "PRESERVE",
        "createTime": "2026-10-14 10:00:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  },
  "traceId": "a1b2c3d4"
}
```



## 9. 订阅模块

### 9.1 获取订阅计划

| 项目 | 内容                   |
| :--- | :--------------------- |
| 方法 | GET                    |
| 路径 | `/subscriptions/plans` |
| 鉴权 | 否                     |
| 说明 | 获取所有可用订阅计划   |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "每月精选",
      "period": "MONTHLY",
      "price": 128.00,
      "description": "每月配送一次，250g 精选咖啡豆"
    }
  ],
  "traceId": "a1b2c3d4"
}
```



### 9.2 创建订阅

| 项目 | 内容             |
| :--- | :--------------- |
| 方法 | POST             |
| 路径 | `/subscriptions` |
| 鉴权 | 是               |
| 说明 | 用户创建订阅     |

**请求体**

json

```
{
  "planId": 1,
  "roastLevel": "LIGHT",
  "flavorTags": ["花果"],
  "processMethod": "WASHED"
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "subscriptionId": 1,
    "planName": "每月精选",
    "status": "ACTIVE",
    "nextDeliveryDate": "2026-11-14"
  },
  "traceId": "a1b2c3d4"
}
```



### 9.3 我的订阅

| 项目 | 内容                |
| :--- | :------------------ |
| 方法 | GET                 |
| 路径 | `/subscriptions/my` |
| 鉴权 | 是                  |
| 说明 | 查询当前用户订阅    |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "planName": "每月精选",
      "period": "MONTHLY",
      "status": "ACTIVE",
      "nextDeliveryDate": "2026-11-14",
      "createTime": "2026-10-14 10:00:00"
    }
  ],
  "traceId": "a1b2c3d4"
}
```



### 9.4 暂停订阅

| 项目 | 内容                        |
| :--- | :-------------------------- |
| 方法 | PUT                         |
| 路径 | `/subscriptions/{id}/pause` |
| 鉴权 | 是                          |
| 说明 | 暂停订阅，不再生成订单      |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "status": "PAUSED"
  },
  "traceId": "a1b2c3d4"
}
```



### 9.5 跳过本次配送

| 项目 | 内容                       |
| :--- | :------------------------- |
| 方法 | PUT                        |
| 路径 | `/subscriptions/{id}/skip` |
| 鉴权 | 是                         |
| 说明 | 跳过本次配送，下次周期顺延 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "status": "ACTIVE",
    "nextDeliveryDate": "2026-12-14"
  },
  "traceId": "a1b2c3d4"
}
```



### 9.6 取消订阅

| 项目 | 内容                         |
| :--- | :--------------------------- |
| 方法 | PUT                          |
| 路径 | `/subscriptions/{id}/cancel` |
| 鉴权 | 是                           |
| 说明 | 取消订阅                     |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "status": "CANCELLED"
  },
  "traceId": "a1b2c3d4"
}
```



## 10. 后台管理模块（P1）

### 10.1 商品管理

| 项目 | 内容                   |
| :--- | :--------------------- |
| 方法 | GET                    |
| 路径 | `/admin/products`      |
| 鉴权 | 是（管理员）           |
| 说明 | 分页查询商品，支持筛选 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "埃塞俄比亚 耶加雪菲",
        "roastLevel": "LIGHT",
        "status": 1,
        "createTime": "2026-10-14 10:00:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  },
  "traceId": "a1b2c3d4"
}
```



### 10.2 新增商品

| 项目 | 内容              |
| :--- | :---------------- |
| 方法 | POST              |
| 路径 | `/admin/products` |
| 鉴权 | 是（管理员）      |
| 说明 | 新增咖啡商品      |

**请求体**

json

```
{
  "name": "哥伦比亚 慧兰",
  "origin": "哥伦比亚",
  "roastLevel": "MEDIUM",
  "flavorTags": ["坚果", "焦糖"],
  "processMethod": "WASHED",
  "description": "经典哥伦比亚风味",
  "coverUrl": "https://xxx.com/2.jpg"
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 2,
    "name": "哥伦比亚 慧兰"
  },
  "traceId": "a1b2c3d4"
}
```



### 10.3 修改商品

| 项目 | 内容                   |
| :--- | :--------------------- |
| 方法 | PUT                    |
| 路径 | `/admin/products/{id}` |
| 鉴权 | 是（管理员）           |
| 说明 | 修改商品信息           |

**请求体**

json

```
{
  "name": "哥伦比亚 慧兰（新）",
  "roastLevel": "MEDIUM",
  "description": "更新描述"
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 2,
    "name": "哥伦比亚 慧兰（新）"
  },
  "traceId": "a1b2c3d4"
}
```



### 10.4 上下架商品

| 项目 | 内容                          |
| :--- | :---------------------------- |
| 方法 | PUT                           |
| 路径 | `/admin/products/{id}/status` |
| 鉴权 | 是（管理员）                  |
| 说明 | 上架或下架商品                |

**请求体**

json

```
{
  "status": 0
}
```



**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 2,
    "status": 0
  },
  "traceId": "a1b2c3d4"
}
```



### 10.5 订单管理

| 项目 | 内容             |
| :--- | :--------------- |
| 方法 | GET              |
| 路径 | `/admin/orders`  |
| 鉴权 | 是（管理员）     |
| 说明 | 分页查询所有订单 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "orderNo": "COF202610140001",
        "userId": 1,
        "payAmount": 136.00,
        "status": "已支付",
        "createTime": "2026-10-14 10:00:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  },
  "traceId": "a1b2c3d4"
}
```



### 10.6 订单发货

| 项目 | 内容                           |
| :--- | :----------------------------- |
| 方法 | PUT                            |
| 路径 | `/admin/orders/{orderNo}/ship` |
| 鉴权 | 是（管理员）                   |
| 说明 | 订单发货                       |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "orderNo": "COF202610140001",
    "status": "已发货"
  },
  "traceId": "a1b2c3d4"
}
```



### 10.7 订阅管理

| 项目 | 内容                   |
| :--- | :--------------------- |
| 方法 | GET                    |
| 路径 | `/admin/subscriptions` |
| 鉴权 | 是（管理员）           |
| 说明 | 分页查询所有订阅       |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "planName": "每月精选",
        "status": "ACTIVE",
        "nextDeliveryDate": "2026-11-14"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  },
  "traceId": "a1b2c3d4"
}
```



### 10.8 数据看板

| 项目 | 内容                     |
| :--- | :----------------------- |
| 方法 | GET                      |
| 路径 | `/admin/dashboard`       |
| 鉴权 | 是（管理员）             |
| 说明 | 销售、订单、订阅统计数据 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "totalSales": 12345.67,
    "orderCount": 100,
    "subscriptionCount": 20,
    "activeSubscriptionCount": 15
  },
  "traceId": "a1b2c3d4"
}
```



## 11. 通知模块（P1）

### 11.1 我的通知

| 项目 | 内容                 |
| :--- | :------------------- |
| 方法 | GET                  |
| 路径 | `/notifications`     |
| 鉴权 | 是                   |
| 说明 | 分页查询当前用户通知 |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "订单已支付",
        "content": "您的订单 COF202610140001 已支付成功",
        "isRead": 0,
        "createTime": "2026-10-14 10:05:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  },
  "traceId": "a1b2c3d4"
}
```



### 11.2 标记已读

| 项目 | 内容                       |
| :--- | :------------------------- |
| 方法 | PUT                        |
| 路径 | `/notifications/{id}/read` |
| 鉴权 | 是                         |
| 说明 | 标记通知为已读             |

**响应示例**

json

```
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "isRead": 1
  },
  "traceId": "a1b2c3d4"
}
```



## 12. 修订记录

| 版本 | 日期       | 修订人   | 修订说明                                   |
| :--- | :--------- | :------- | :----------------------------------------- |
| v1.0 | 2026-10-14 | 项目经理 | 初始版本                                   |
| v1.1 | 2026-10-14 | 项目经理 | 补全所有接口的响应示例，确保前后端契约完整 |
| v1.2 | 2026-10-15 | 项目经理 | 补充注册/登录相关错误码（4007-4010）       |