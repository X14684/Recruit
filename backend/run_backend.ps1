# 停止所有 Java 进程
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 5

# 编译
Write-Host "编译中..."
cd d:\xt\Recruit\backend
mvn compile -q

# 启动后端并记录日志
Write-Host "启动后端..."
$logFile = "d:\xt\Recruit\backend\backend.log"
$proc = Start-Process -FilePath cmd -ArgumentList "/c mvn spring-boot:run > $logFile 2>&1" -PassThru -NoNewWindow

# 等待启动
Start-Sleep -Seconds 60

# 检查是否成功
$port = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Where-Object {$_.State -eq "Listen"}
if ($port) {
    Write-Host "后端启动成功，PID: $($port.OwningProcess)"
} else {
    Write-Host "后端启动失败，查看日志："
    Get-Content $logFile -ErrorAction SilentlyContinue | Select-Object -Last 50
}
