# Digital Wallet API

A comprehensive Spring Boot REST API for digital wallet management with separate Customer and Employee entities, role-based authentication, and secure transaction processing.

## 🏗️ Architecture

### **Entity Structure**
- **Customer**: TCKN-based authentication, no roles
- **Employee**: EmployeeID-based authentication, with roles (EMPLOYEE, MANAGER, ADMIN)
- **Wallet**: Customer-owned digital wallets with balance management
- **Transaction**: Deposit/withdrawal transactions with approval workflow

### **Security Model**
- **Customers**: Can only access their own wallets and transactions
- **Employees**: Can manage all customers, wallets, and transactions
- **ADMIN**: Can manage employees and has full system access

## 🚀 Quick Start

### **Prerequisites**
- Java 17 or higher
- Maven 3.6+
- IntelliJ IDEA (recommended)

### **Installation & Running**

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd DigitalWallet
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`
   - Database URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: `password`

## 🔐 Authentication

### **Customer Login**
- **Username**: TCKN (e.g., `12345678901`)
- **Password**: `password`
- **Role**: `ROLE_CUSTOMER`

### **Employee Login**
- **Username**: EmployeeID (e.g., `EMP001`)
- **Password**: `password`
- **Role**: `ROLE_ADMIN` (for the test employee)

### **Login Endpoint**
```
POST /api/auth/login
Content-Type: application/x-www-form-urlencoded

username=12345678901&password=password
```

## 📋 API Endpoints

### **Authentication**
- `POST /api/auth/login` - Login (form-based)
- `POST /api/auth/logout` - Logout

### **Customer Management (Employee Only)**
- `POST /api/customers` - Create customer
- `GET /api/customers` - Get all customers
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/tckn/{tckn}` - Get customer by TCKN
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer
- `GET /api/customers/exists/tckn/{tckn}` - Check if customer exists

### **Employee Management (ADMIN Only)**
- `POST /api/employees` - Create employee
- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get employee by ID
- `GET /api/employees/employee-id/{employeeId}` - Get employee by Employee ID
- `GET /api/employees/role/{role}` - Get employees by role
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee
- `GET /api/employees/exists/employee-id/{employeeId}` - Check if employee exists

### **Wallet Management**
- `POST /api/wallets/customer/{customerId}` - Create wallet for customer
- `POST /api/wallets/me` - Create wallet for current customer
- `GET /api/wallets/{id}` - Get wallet by ID
- `GET /api/wallets/customer/{customerId}` - Get wallets by customer
- `GET /api/wallets` - Get all wallets (Employee only)
- `PUT /api/wallets/{id}/balance` - Update wallet balance (Employee only)
- `POST /api/wallets/{id}/balance/add` - Add to wallet balance (Employee only)
- `POST /api/wallets/{id}/balance/deduct` - Deduct from wallet balance (Employee only)
- `PUT /api/wallets/{id}/status` - Update wallet status
- `DELETE /api/wallets/{id}` - Delete wallet

### **Transaction Management**
- `POST /api/transactions/deposit` - Create deposit transaction
- `POST /api/transactions/withdraw` - Create withdrawal transaction
- `GET /api/transactions/wallet/{walletId}` - Get transactions by wallet
- `GET /api/transactions` - Get all transactions
- `PUT /api/transactions/{id}/approve` - Approve transaction
- `PUT /api/transactions/{id}/deny` - Deny transaction
- `GET /api/transactions/pending` - Get pending transactions

## 🧪 Testing

### **Run Tests**
```bash
mvn test
```

### **Test Coverage**
- **34 tests** covering all major functionality
- **Repository tests** - Data access layer
- **Service tests** - Business logic layer
- **Security tests** - Authentication and authorization
- **Entity tests** - Data model validation

### **Test Data**
The application automatically creates test data on startup:
- **Customer**: TCKN `12345678901`, password `password`
- **Employee**: EmployeeID `EMP001`, password `password`, role `ADMIN`

## 📊 Database Schema

### **Customers Table**
```sql
CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    tckn VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```

### **Employees Table**
```sql
CREATE TABLE employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    employee_id VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);
```

### **Wallets Table**
```sql
CREATE TABLE wallets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wallet_name VARCHAR(255) NOT NULL,
    balance DECIMAL(19,2) DEFAULT 0.00,
    currency VARCHAR(255) NOT NULL,
    active_for_shopping BOOLEAN DEFAULT TRUE,
    active_for_withdraw BOOLEAN DEFAULT TRUE,
    customer_id BIGINT,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);
```

### **Transactions Table**
```sql
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    amount DECIMAL(19,2) NOT NULL,
    type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    opposite_party_type VARCHAR(255) NOT NULL,
    opposite_party VARCHAR(255) NOT NULL,
    wallet_id BIGINT,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);
```

## 🔧 Configuration

### **Application Properties**
```properties
# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Server
server.port=8080
```

## 🛡️ Security Features

### **Authentication**
- **Form-based login** with Spring Security
- **Custom AuthenticationProvider** supporting both Customer and Employee authentication
- **Session-based security** with proper logout handling

### **Authorization**
- **Role-based access control** (RBAC)
- **URL-level security** with Spring Security configuration
- **Method-level security** with explicit authorization checks
- **Fine-grained permissions** for different user types

### **Data Protection**
- **Password fields** not exposed in DTOs
- **Customer isolation** - customers can only access their own data
- **Employee privileges** - employees can access all customer data
- **Admin privileges** - admins can manage employees

## 📈 Key Features

### **Customer Features**
- ✅ Create and manage personal wallets
- ✅ View transaction history
- ✅ Deposit and withdraw funds
- ✅ Secure authentication with TCKN

### **Employee Features**
- ✅ Manage all customers
- ✅ Create wallets for customers
- ✅ Process transactions
- ✅ View all system data
- ✅ Direct balance management

### **Admin Features**
- ✅ Manage employees
- ✅ Full system access
- ✅ Employee role management
- ✅ System administration

## 🚀 Production Considerations

### **Security Enhancements**
- [ ] Implement password hashing (BCrypt)
- [ ] Add JWT token authentication
- [ ] Implement rate limiting
- [ ] Add input validation and sanitization
- [ ] Enable HTTPS

### **Database**
- [ ] Migrate to PostgreSQL/MySQL
- [ ] Add database migrations
- [ ] Implement connection pooling
- [ ] Add database backup strategy

### **Monitoring**
- [ ] Add application metrics
- [ ] Implement logging strategy
- [ ] Add health checks
- [ ] Set up monitoring and alerting

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For questions or issues, please create an issue in the repository.

---

**Built with ❤️ using Spring Boot 3.5.4 and Java 17** 