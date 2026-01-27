#!/bin/bash

echo "=== 启动蔬菜销售系统 ==="
echo ""

# 设置 Java 环境
export JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || echo "/Library/Java/JavaVirtualMachines/liberica-jdk-17.jdk/Contents/Home")
export PATH=$JAVA_HOME/bin:$PATH

echo "Java 环境:"
java -version
echo ""

echo "Maven 环境:"
mvn -v | head -3
echo ""

echo "Node.js 环境:"
node -v
echo ""

# 启动后端
echo "启动后端服务..."
cd ape-admin
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo "后端启动中 (PID: $BACKEND_PID)..."
echo "日志: backend.log"
echo ""

# 等待后端启动
echo -n "等待后端服务启动"
for i in {1..30}; do
    if curl -s http://localhost:8081 > /dev/null 2>&1; then
        echo -e "\n✅ 后端服务已就绪: http://localhost:8081"
        break
    fi
    echo -n "."
    sleep 2
done
echo ""

# 启动前端
echo "启动前端服务..."
cd ../web-admin

# 检查依赖
if [ ! -d "node_modules" ]; then
    echo "安装前端依赖..."
    npm install
fi

echo "前端服务启动中..."
echo "✅ 前端界面: http://localhost:3000"
echo "按 Ctrl+C 停止所有服务"

npm run dev

# 清理
echo "停止后端服务..."
kill $BACKEND_PID 2>/dev/null
echo "完成"