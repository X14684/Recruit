# =====================================================
# 校园求职招聘系统 - 数据库初始化脚本（PowerShell版本）
# 使用方法：.\init_database.ps1
# =====================================================

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host " 校园求职招聘系统 - 数据库初始化" -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host ""

# 检查MySQL是否已安装
Write-Host "[1/3] 检查MySQL环境..." -ForegroundColor Yellow
$mysqlPath = where.exe mysql 2>$null
if ($null -eq $mysqlPath) {
    Write-Host "[错误] 未找到MySQL命令行工具，请确保已安装MySQL并添加到PATH环境变量" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}
Write-Host "[成功] MySQL已安装: $mysqlPath" -ForegroundColor Green
Write-Host ""

# 读取SQL文件
$sqlFile = Join-Path $PSScriptRoot "src\main\resources\db\init.sql"
if (-not (Test-Path $sqlFile)) {
    Write-Host "[错误] 找不到SQL文件: $sqlFile" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}
Write-Host "[2/3] 读取SQL文件..." -ForegroundColor Yellow
Write-Host "[成功] SQL文件路径: $sqlFile" -ForegroundColor Green
Write-Host ""

# 执行SQL脚本
Write-Host "[3/3] 初始化数据库..." -ForegroundColor Yellow
Write-Host "请输入MySQL root密码（密码不会显示在屏幕上）:" -ForegroundColor Cyan
$password = Read-Host -AsSecureString
$passwordText = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))

$arguments = @(
    "-u",
    "root",
    "-p$passwordText",
    "-e",
    "source $sqlFile"
)

try {
    & mysql @arguments 2>&1 | Tee-Object -Variable output
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "====================================================" -ForegroundColor Green
        Write-Host "[成功] 数据库初始化完成！" -ForegroundColor Green
        Write-Host "====================================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "数据库名称: campus_recruitment" -ForegroundColor White
        Write-Host "测试账号密码均为: 123456" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "默认测试账号:" -ForegroundColor White
        Write-Host "  - 管理员: admin" -ForegroundColor Gray
        Write-Host "  - 学院: college_cs, college_eco, college_me, college_ee" -ForegroundColor Gray
        Write-Host "  - 企业: company_ali, company_tx, company_byte, company_hw, company_mt" -ForegroundColor Gray
        Write-Host "  - 学生: student001, student002, student003" -ForegroundColor Gray
        Write-Host ""
        Write-Host "后端API地址: http://localhost:8081/api" -ForegroundColor Cyan
        Write-Host "API文档地址: http://localhost:8081/api/doc.html" -ForegroundColor Cyan
        Write-Host ""
    } else {
        Write-Host ""
        Write-Host "[错误] 数据库初始化失败，错误代码: $LASTEXITCODE" -ForegroundColor Red
        Write-Host "请检查：" -ForegroundColor Yellow
        Write-Host "  1. MySQL服务是否已启动" -ForegroundColor Gray
        Write-Host "  2. root密码是否正确" -ForegroundColor Gray
        Write-Host "  3. init.sql文件路径是否正确" -ForegroundColor Gray
    }
} catch {
    Write-Host ""
    Write-Host "[错误] 执行SQL时发生异常: $_" -ForegroundColor Red
} finally {
    # 清空密码
    $passwordText = $null
}

Write-Host ""
Read-Host "按回车键退出"
