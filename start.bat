@echo off
chcp 65001 >nul
echo ==========================================
echo   蔬菜销售系统 - 启动脚本 (Windows)
echo ==========================================
echo.

echo 请选择启动方式：
echo 1) 仅启动后端服务
echo 2) 仅启动前端服务
echo 3) 同时启动后端和前端（推荐）
set /p choice=请输入选项 (1/2/3): 

if "%choice%"=="1" goto backend
if "%choice%"=="2" goto frontend
if "%choice%"=="3" goto both
goto end

:backend
echo.
echo 启动后端服务...
cd ape-admin
call mvn spring-boot:run
goto end

:frontend
echo.
echo 启动前端服务...
cd web-admin
call npm run dev
goto end

:both
echo.
echo 启动后端服务...
start "后端服务" cmd /k "cd ape-admin && mvn spring-boot:run"
timeout /t 10 /nobreak >nul
echo.
echo 启动前端服务...
cd web-admin
call npm run dev
goto end

:end
pause
