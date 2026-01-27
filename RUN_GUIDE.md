# 蔬菜销售系统 - 运行指南

## 📋 环境要求

### 必需环境
- **JDK 1.8+** （推荐 JDK 1.8 或 JDK 11）
- **Maven 3.6+**
- **Node.js 16+** （推荐 Node.js 18+）
- **MySQL 5.7+** 或 **MySQL 8.0+**
- **Redis 5.0+**

### 开发工具（可选）
- **IntelliJ IDEA** 或 **Eclipse**（后端开发）
- **VS Code** 或 **WebStorm**（前端开发）

---

## 🗄️ 数据库准备

### 1. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS `ape-vegetable` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 导入SQL脚本

执行项目根目录下的SQL脚本：

```bash
# 方式1：使用命令行
mysql -u root -p ape-vegetable < sql/vegetable_sale_tables.sql

# 方式2：使用MySQL客户端工具（如Navicat、DBeaver）
# 打开 sql/vegetable_sale_tables.sql 文件，在客户端中执行
```

### 3. 验证数据库

检查以下表是否创建成功：
- `ape_user` - 用户表
- `ape_vegetable` - 商品表
- `ape_vegetable_order` - 订单表
- `ape_stock` - 库存表
- 等其他业务表...

---

## ⚙️ 后端配置

### 1. 修改数据库配置

编辑文件：`ape-admin/src/main/resources/application-dev.yml`

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ape-vegetable?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root          # 修改为你的MySQL用户名
        password: xia19760705   # 修改为你的MySQL密码
```

### 2. 修改Redis配置

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: 123456  # 如果Redis没有密码，设置为空字符串 ""
```

---

## 🚀 启动后端服务

### 方式1：使用Maven命令（推荐）

```bash
# 1. 进入项目根目录
cd /Users/leo/Desktop/vegetable-sale-master

# 2. 编译项目（首次运行）
mvn clean install -DskipTests

# 3. 启动后端服务
cd ape-admin
mvn spring-boot:run
```

### 方式2：使用IDE启动

1. 使用 **IntelliJ IDEA** 打开项目
2. 找到主启动类：`ape-admin/src/main/java/com/ape/apeadmin/ApeAdminApplication.java`
3. 右键 → Run 'ApeAdminApplication'

### 方式3：打包后运行

```bash
# 1. 打包项目
mvn clean package -DskipTests

# 2. 运行jar包
cd ape-admin/target
java -jar ape-admin-0.0.1-SNAPSHOT.jar
```

### 验证后端启动

看到以下日志表示启动成功：

```
Started ApeAdminApplication in X.XXX seconds
```

**默认端口：** `8081`（在 application.yml 中配置）

**访问测试：**
- API文档（如果有）：http://localhost:8081/swagger-ui.html
- Druid监控：http://localhost:8081/druid/index.html（用户名：ape，密码：123456）

---

## 🎨 启动前端服务

### 1. 安装依赖

```bash
# 进入前端目录
cd web-admin

# 安装依赖（首次运行）
npm install
# 或使用 yarn
yarn install
```

### 2. 修改代理配置（如果需要）

编辑文件：`web-admin/vite.config.js`

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8081',  // 确保与后端端口一致（默认8081）
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

### 3. 启动开发服务器

```bash
# 在 web-admin 目录下执行
npm run dev
# 或使用 yarn
yarn dev
```

### 验证前端启动

看到以下信息表示启动成功：

```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

**访问地址：**
- 前台首页：http://localhost:3000
- 登录页面：http://localhost:3000/login
- 管理后台：http://localhost:3000/admin

---

## 🔑 默认账号

### 管理员账号
- **用户名：** `admin`
- **密码：** `123456`（如果数据库中有默认数据）

### 测试账号
如果没有默认数据，需要先注册账号：
1. 访问：http://localhost:3000/register
2. 注册一个账号（选择用户类型：采购商/供应商/管理员）

---

## 📱 功能测试

### 前台功能测试流程

1. **用户注册/登录**
   - 访问 http://localhost:3000/register 注册账号
   - 访问 http://localhost:3000/login 登录

2. **浏览商品**
   - 首页查看推荐商品
   - 分类页面浏览商品分类
   - 商品列表页面筛选、排序
   - 商品详情页面查看详情

3. **购物流程**
   - 添加商品到购物车
   - 购物车页面管理商品
   - 结算页面选择地址、提交订单
   - 订单列表查看订单
   - 订单详情查看详情、支付、确认收货

4. **个人中心**
   - 修改个人信息
   - 管理收货地址
   - 查看收藏商品
   - 修改密码

### 后台功能测试流程

1. **登录管理后台**
   - 使用管理员账号登录
   - 访问 http://localhost:3000/admin

2. **商品管理**
   - 添加/编辑/删除商品
   - 商品上下架
   - 商品审核（如果是供应商提交的）

3. **订单管理**
   - 查看订单列表
   - 订单发货
   - 订单详情查看

4. **用户管理**
   - 查看用户列表
   - 编辑用户信息
   - 重置用户密码

---

## ⚠️ 常见问题

### 1. 后端启动失败

**问题：数据库连接失败**
```
解决方案：
1. 检查MySQL服务是否启动
2. 检查数据库用户名密码是否正确
3. 检查数据库名称是否为 ape-vegetable
4. 检查MySQL端口是否为 3306
```

**问题：Redis连接失败**
```
解决方案：
1. 检查Redis服务是否启动：redis-cli ping（应该返回 PONG）
2. 检查Redis密码是否正确
3. 如果Redis没有密码，将配置中的 password 设置为空字符串 ""
```

**问题：端口被占用**
```
解决方案：
1. 修改 application.yml 中的 server.port（默认8081）
2. 或关闭占用端口的程序
3. 查看端口占用：lsof -i :8081（Mac/Linux）
```

### 2. 前端启动失败

**问题：npm install 失败**
```
解决方案：
1. 清除缓存：npm cache clean --force
2. 删除 node_modules 和 package-lock.json
3. 重新安装：npm install
4. 或使用国内镜像：npm install --registry=https://registry.npmmirror.com
```

**问题：端口被占用**
```
解决方案：
1. 修改 vite.config.js 中的 server.port
2. 或关闭占用端口的程序
```

**问题：API请求失败（404）**
```
解决方案：
1. 检查后端服务是否启动
2. 检查 vite.config.js 中的 proxy 配置（默认指向8081）
3. 检查后端端口是否为 8081（查看 application.yml）
4. 检查浏览器控制台错误信息
```

### 3. 数据库问题

**问题：表不存在**
```
解决方案：
1. 确认已执行 SQL 脚本
2. 检查数据库名称是否正确
3. 检查表是否创建成功：SHOW TABLES;
```

**问题：字符编码问题**
```
解决方案：
1. 确保数据库字符集为 utf8mb4
2. 确保连接URL包含 useUnicode=true&characterEncoding=utf8
```

### 4. 跨域问题

如果遇到跨域问题，后端已经配置了CORS，如果还有问题：

1. 检查 `CorsConfig.java` 配置
2. 确保前端使用代理（vite.config.js中的proxy配置）

---

## 🔧 开发工具配置

### IntelliJ IDEA 配置

1. **导入项目**
   - File → Open → 选择项目根目录
   - 等待Maven自动下载依赖

2. **配置JDK**
   - File → Project Structure → Project
   - 设置 Project SDK 为 JDK 1.8 或更高版本

3. **配置运行配置**
   - Run → Edit Configurations
   - 添加 Spring Boot 配置
   - Main class: `com.ape.apeadmin.ApeAdminApplication`
   - Working directory: `$MODULE_DIR$`

### VS Code 配置

1. **安装插件**
   - Vetur 或 Volar（Vue 3支持）
   - ESLint
   - Prettier

2. **配置调试**
   - 创建 `.vscode/launch.json`
   - 配置前端调试

---

## 📦 生产环境部署

### 后端部署

```bash
# 1. 打包
mvn clean package -DskipTests

# 2. 上传 jar 包到服务器
scp ape-admin/target/ape-admin-0.0.1-SNAPSHOT.jar user@server:/path/to/app/

# 3. 运行
java -jar -Dspring.profiles.active=prod ape-admin-0.0.1-SNAPSHOT.jar
```

### 前端部署

```bash
# 1. 构建生产版本
cd web-admin
npm run build

# 2. 部署 dist 目录到 Nginx 或其他静态服务器
# 配置 Nginx 反向代理到后端 API
```

---

## 📞 技术支持

如果遇到问题：

1. 查看日志文件
2. 检查控制台错误信息
3. 查看数据库连接状态
4. 检查Redis连接状态

---

## ✅ 启动检查清单

- [ ] MySQL服务已启动
- [ ] Redis服务已启动
- [ ] 数据库已创建并导入SQL脚本
- [ ] 后端配置文件已修改（数据库、Redis）
- [ ] 后端服务启动成功（端口8081）
- [ ] 前端依赖已安装
- [ ] 前端代理配置正确
- [ ] 前端服务启动成功（端口3000）
- [ ] 可以访问 http://localhost:3000

---

**祝您使用愉快！** 🎉
