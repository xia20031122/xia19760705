# 🚀 快速启动指南

## 一键启动（推荐）

### Mac/Linux 用户

```bash
# 在项目根目录执行
./start.sh
```

### Windows 用户

```cmd
# 在项目根目录执行
start.bat
```

---

## 📝 手动启动步骤

### 第一步：准备环境

1. **启动MySQL**
   ```bash
   # Mac
   brew services start mysql
   # 或
   mysql.server start
   
   # Linux
   sudo systemctl start mysql
   
   # Windows
   # 在服务管理器中启动MySQL服务
   ```

2. **启动Redis**
   ```bash
   # Mac
   brew services start redis
   # 或
   redis-server
   
   # Linux
   sudo systemctl start redis
   
   # Windows
   # 在服务管理器中启动Redis服务
   ```

3. **创建数据库并导入SQL**
   ```bash
   mysql -u root -p
   CREATE DATABASE `ape-vegetable` DEFAULT CHARACTER SET utf8mb4;
   exit
   
   mysql -u root -p ape-vegetable < sql/vegetable_sale_tables.sql
   ```

### 第二步：配置后端

编辑 `ape-admin/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    druid:
      master:
        username: root          # 你的MySQL用户名
        password: your_password  # 你的MySQL密码
  redis:
    password: your_redis_password  # 你的Redis密码（如果没有密码，设为空字符串 ""）
```

### 第三步：启动后端

```bash
cd ape-admin
mvn spring-boot:run
```

看到 `Started ApeAdminApplication` 表示启动成功！

**后端地址：** http://localhost:8081

### 第四步：启动前端

```bash
# 新开一个终端窗口
cd web-admin
npm install  # 首次运行需要安装依赖
npm run dev
```

看到 `Local: http://localhost:3000/` 表示启动成功！

**前端地址：** http://localhost:3000

---

## ✅ 验证启动

1. 打开浏览器访问：http://localhost:3000
2. 应该能看到首页
3. 点击登录，注册一个新账号
4. 登录后可以浏览商品、添加购物车等

---

## 🔧 常见问题快速解决

### 后端启动失败

**问题：数据库连接失败**
- 检查MySQL是否启动
- 检查用户名密码是否正确
- 检查数据库是否创建

**问题：Redis连接失败**
- 检查Redis是否启动：`redis-cli ping`
- 检查密码是否正确

### 前端启动失败

**问题：npm install 失败**
```bash
# 清除缓存重新安装
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

**问题：端口被占用**
- 修改 `vite.config.js` 中的 `port: 3000` 为其他端口

### API请求失败

- 检查后端是否启动（访问 http://localhost:8081）
- 检查 `vite.config.js` 中的 proxy 配置是否正确

---

## 📞 需要帮助？

查看详细文档：`RUN_GUIDE.md`

---

**祝您使用愉快！** 🎉
