@echo off
echo 🐳 Digital Wallet API - Docker Build and Run
echo =============================================

echo.
echo 📦 Building the application...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Build failed! Please check the errors above.
    pause
    exit /b 1
)

echo.
echo ✅ Build successful!
echo.
echo 🚀 Starting Docker containers...
docker-compose up --build

echo.
echo 🎉 Application is running at http://localhost:8080
echo 📊 Health check: http://localhost:8080/actuator/health
echo 🗄️  H2 Console: http://localhost:8080/h2-console
echo.
echo Press Ctrl+C to stop the application
pause 