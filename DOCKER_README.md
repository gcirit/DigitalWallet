# 🐳 Digital Wallet API - Docker Setup

## 🚀 Quick Start with Docker

### Prerequisites
- **Docker Desktop** installed and running
- **Git** (to clone the repository)

### 🎯 One-Command Setup

```bash
# Clone the repository (if not already done)
git clone <repository-url>
cd DigitalWallet

# Build and run with Docker Compose
docker-compose up --build
```

That's it! The application will be available at `http://localhost:8080`

You can log-in with: 
- **user: EMP001**  
- **password: password**

## 📋 Available Endpoints

### API Endpoints
- **Base URL**: `http://localhost:8080`
- **API Documentation**: `http://localhost:8080/swagger-ui.html` (if Swagger is added)
- **Health Check**: `http://localhost:8080/actuator/health`

### Database Console
- **H2 Console**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:walletdb`
- **Username**: `sa`
- **Password**: `password`

## 🛠️ Docker Commands

### Build the Application
```bash
# Build the JAR file first
mvn clean package

# Build Docker image
docker build -t digitalwallet .
```

### Run with Docker Compose
```bash
# Start all services
docker-compose up

# Start in background
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f digitalwallet
```

### Run with Docker (without compose)
```bash
# Build and run
docker build -t digitalwallet .
docker run -p 8080:8080 digitalwallet
```

## 🔧 Configuration

### Environment Variables
You can customize the application by setting environment variables:

```bash
# Set custom port
docker run -p 9090:8080 -e SERVER_PORT=8080 digitalwallet

# Set custom JVM options
docker run -p 8080:8080 -e JAVA_OPTS="-Xmx1g -Xms512m" digitalwallet
```

### Docker Compose Environment
Edit `docker-compose.yml` to customize:

```yaml
services:
  digitalwallet:
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - JAVA_OPTS=-Xmx1g -Xms512m
    ports:
      - "8080:8080"  # Change port if needed
```

## 📊 Monitoring

### Health Check
The application includes health checks:
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Expected response:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

### View Logs
```bash
# View application logs
docker-compose logs digitalwallet

# Follow logs in real-time
docker-compose logs -f digitalwallet

# View logs for specific container
docker logs <container-id>
```

## 🔐 Authentication

The application uses Spring Security with role-based access control. You need to authenticate before accessing most endpoints.

### Default Admin Account
- **Username**: `EMP001`
- **Password**: `password` (any password works in this implementation)
- **Role**: `ADMIN`

### Authentication Methods

#### 1. Form Authentication (Recommended)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=EMP001&password=password"
```

#### 2. Basic Authentication
```bash
curl -u EMP001:password http://localhost:8080/api/customers
```

#### 3. JSON Authentication
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "EMP001",
    "password": "password"
  }'
```

### Role-Based Access Control
- **ADMIN**: Can access all endpoints
- **EMPLOYEE**: Can access customer, wallet, and transaction endpoints
- **CUSTOMER**: Can access their own data only

## 🧪 Testing the Application

### 1. Health Check (Public - No Authentication Required)
```bash
curl http://localhost:8080/actuator/health
```

### 2. Authenticate First
```bash
# Login with admin account
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=EMP001&password=password"
```

### 3. Test API Endpoints (After Authentication)
```bash
# Test customers endpoint (requires EMPLOYEE or ADMIN role)
curl http://localhost:8080/api/customers

# Test employees endpoint (requires ADMIN role)
curl http://localhost:8080/api/employees

# Test wallets endpoint
curl http://localhost:8080/api/wallets

# Test transactions endpoint
curl http://localhost:8080/api/transactions
```

### 4. Create a Customer (After Authentication)
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890"
  }'
```

### 5. Access H2 Console
1. Open browser and go to `http://localhost:8080/h2-console`
2. Use these credentials:
   - **JDBC URL**: `jdbc:h2:mem:walletdb`
   - **Username**: `sa`
   - **Password**: `password`

## 🔍 Troubleshooting

### Common Issues

**1. Authentication Required**
- Most endpoints require authentication
- Use the provided admin credentials: `EMP001` / `password`
- Health endpoint (`/actuator/health`) is public and doesn't require authentication

**2. Port Already in Use**
```bash
# Check what's using port 8080
netstat -ano | findstr :8080

# Use different port
docker run -p 8081:8080 digitalwallet
```

**3. Container Won't Start**
```bash
# Check container logs
docker logs <container-id>

# Check if JAR file exists
ls -la target/digital-wallet-1.0.0.jar
```

**4. Database Connection Issues**
- Verify H2 console is accessible at `http://localhost:8080/h2-console`
- Check application logs for database errors

**5. Spring Boot Configuration Issues**
- If you see "spring.profiles" errors, rebuild the JAR file:
```bash
docker run --rm -v ${PWD}:/app -w /app maven:3.9 mvn clean package -DskipTests
docker-compose up --build
```

### Debug Commands
```bash
# Enter running container
docker exec -it <container-id> /bin/bash

# Check container status
docker ps -a

# Remove all containers and images (clean slate)
docker system prune -a
```

## 📁 Project Structure
```
DigitalWallet/
├── Dockerfile                 # Docker image definition
├── docker-compose.yml         # Multi-container setup
├── .dockerignore             # Files to exclude from build
├── src/
│   └── main/
│       └── resources/
│           └── application-docker.yml  # Docker profile config
└── target/
    └── digitalwallet-0.0.1-SNAPSHOT.jar  # Built JAR file
```

## 🎯 Next Steps

After running the application:
1. **Test the API** using the provided endpoints
2. **Import Postman collection** for comprehensive testing
3. **Check the API documentation** for detailed usage
4. **Explore the H2 console** to view database data

## 🆘 Support

If you encounter any issues:
1. Check the logs: `docker-compose logs digitalwallet`
2. Verify Docker is running: `docker --version`
3. Ensure port 8080 is available
4. Check the health endpoint: `http://localhost:8080/actuator/health`

Happy Dockerizing! 🐳 