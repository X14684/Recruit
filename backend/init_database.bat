@echo off
chcp 65001 >nul
echo ====================================================
echo 校园求职招聘系统 - 数据库初始化
echo ====================================================
echo.

echo [1/3] 检查MySQL环境...
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到MySQL命令行工具，请确保已安装MySQL并添加到PATH环境变量
    pause
    exit /b 1
)
echo [成功] MySQL已安装
echo.

echo [2/3] 初始化数据库...
echo 请输入MySQL root密码:
mysql -u root -p < "%~dp0src\main\resources\db\init.sql"

if %errorlevel% equ 0 (
    echo.
    echo ====================================================
    echo [成功] 数据库初始化完成！
    echo ====================================================
    echo.
    echo 数据库名称: campus_recruitment
    echo 测试账号密码均为: 123456
    echo.
    echo 默认测试账号:
    echo   - 管理员: admin
    echo   - 学院: college_cs, college_eco, college_me, college_ee
    echo   - 企业: company_ali, company_tx, company_byte, company_hw, company_mt
    echo   - 学生: student001, student002, student003
    echo.
    echo 后端API地址: http://localhost:8081/api
    echo API文档地址: http://localhost:8081/api/doc.html
    echo.
) else (
    echo.
    echo [错误] 数据库初始化失败，请检查：
    echo   1. MySQL服务是否已启动
    echo   2. root密码是否正确
    echo   3. init.sql文件路径是否正确
    echo.
)

pause
