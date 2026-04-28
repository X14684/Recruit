@echo off
echo ============================================
echo 校园招聘系统 - 后端启动脚本
echo ============================================
echo.

echo [1/3] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Java，请安装Java 21或更高版本
    pause
    exit /b 1
)

echo [2/3] 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Maven，请安装Maven
    pause
    exit /b 1
)

echo [3/3] 启动Spring Boot应用...
echo.
echo 应用将在 http://localhost:8080/api 启动
echo API文档地址: http://localhost:8080/api/doc.html
echo.
echo 按 Ctrl+C 停止应用
echo.

cd /d "%~dp0"
mvn spring-boot:run

pause
