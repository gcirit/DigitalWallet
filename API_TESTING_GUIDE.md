# Digital Wallet API Testing Guide

## 🚀 Quick Start

### 1. Start the Application
1. **Run the main application** (`DigitalWalletApplication.java`)
2. **Verify the application starts** at `http://localhost:8080`
3. **Access H2 Console** at `http://localhost:8080/h2-console`

### 2. Import Postman Collection
1. **Open Postman**
2. **Import** the `DigitalWallet_API.postman_collection.json` file
3. **Set the base URL** variable to `http://localhost:8080`

## 📋 API Endpoints Overview

### Customer Management (`/api/customers`)
- ✅ **POST** `/api/customers` - Create customer
- ✅ **GET** `/api/customers/{id}` - Get customer by ID
- ✅ **GET** `/api/customers/tckn/{tckn}` - Get customer by TCKN
- ✅ **GET** `/api/customers` - Get all customers
- ✅ **GET** `/api/customers/role/{role}` - Get customers by role
- ✅ **PUT** `/api/customers/{id}` - Update customer
- ✅ **DELETE** `/api/customers/{id}` - Delete customer
- ✅ **GET** `/api/customers/exists/tckn/{tckn}` - Check if customer exists

### Wallet Management (`/api/wallets`)
- ✅ **POST** `/api/wallets/customer/{customerId}` - Create wallet
- ✅ **GET** `/api/wallets/{id}` - Get wallet by ID
- ✅ **GET** `/api/wallets/customer/{customerId}` - Get customer wallets
- ✅ **GET** `/api/wallets/customer/{customerId}/currency/{currency}` - Get wallets by currency
- ✅ **GET** `/api/wallets` - Get all wallets
- ✅ **PUT** `/api/wallets/{id}/balance` - Update wallet balance
- ✅ **POST** `/api/wallets/{id}/balance/add` - Add to balance
- ✅ **POST** `/api/wallets/{id}/balance/deduct` - Deduct from balance
- ✅ **PUT** `/api/wallets/{id}/status` - Update wallet status
- ✅ **DELETE** `/api/wallets/{id}` - Delete wallet
- ✅ **GET** `/api/wallets/currency/{currency}` - Get wallets by currency
- ✅ **GET** `/api/wallets/active/shopping` - Get active shopping wallets
- ✅ **GET** `/api/wallets/active/withdraw` - Get active withdrawal wallets

### Transaction Management (`/api/transactions`)
- ✅ **POST** `/api/transactions/deposit` - Create deposit transaction
- ✅ **POST** `/api/transactions/withdraw` - Create withdrawal transaction
- ✅ **PUT** `/api/transactions/{id}/approve` - Approve transaction
- ✅ **PUT** `/api/transactions/{id}/deny` - Deny transaction
- ✅ **GET** `/api/transactions/{id}` - Get transaction by ID
- ✅ **GET** `/api/transactions/wallet/{walletId}` - Get wallet transactions
- ✅ **GET** `/api/transactions/wallet/{walletId}/status/{status}` - Get transactions by status
- ✅ **GET** `/api/transactions` - Get all transactions
- ✅ **GET** `/api/transactions/status/{status}` - Get transactions by status
- ✅ **GET** `/api/transactions/pending` - Get pending transactions
- ✅ **GET** `/api/transactions/type/{type}` - Get transactions by type
- ✅ **GET** `/api/transactions/wallet/{walletId}/type/{type}` - Get transactions by type
- ✅ **GET** `/api/transactions/wallet/{walletId}/deposits` - Get deposit transactions
- ✅ **GET** `/api/transactions/wallet/{walletId}/withdrawals` - Get withdrawal transactions

## 🧪 Step-by-Step Testing Workflow

### Step 1: Create a Customer
```bash
POST http://localhost:8080/api/customers
Content-Type: application/json

{
  "name": "John",
  "surname": "Doe", 
  "tckn": "12345678901",
  "role": "CUSTOMER"
}
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "John",
  "surname": "Doe",
  "tckn": "12345678901",
  "role": "CUSTOMER"
}
```

### Step 2: Create a Wallet for the Customer
```bash
POST http://localhost:8080/api/wallets/customer/1
Content-Type: application/json

{
  "walletName": "My Wallet",
  "currency": "TRY",
  "activeForShopping": true,
  "activeForWithdraw": true
}
```

**Expected Response:**
```json
{
  "id": 1,
  "customer": {
    "id": 1,
    "name": "John",
    "surname": "Doe",
    "tckn": "12345678901",
    "role": "CUSTOMER"
  },
  "walletName": "My Wallet",
  "currency": "TRY",
  "activeForShopping": true,
  "activeForWithdraw": true,
  "balance": 0.00,
  "usableBalance": 0.00
}
```

### Step 3: Add Balance to Wallet
```bash
POST http://localhost:8080/api/wallets/1/balance/add
Content-Type: application/json

1000.00
```

**Expected Response:**
```json
{
  "id": 1,
  "balance": 1000.00,
  "usableBalance": 1000.00,
  // ... other fields
}
```

### Step 4: Create a Deposit Transaction
```bash
POST http://localhost:8080/api/transactions/deposit?walletId=1&amount=500.00&oppositePartyType=IBAN&oppositeParty=TR123456789
```

**Expected Response:**
```json
{
  "id": 1,
  "wallet": {
    "id": 1,
    // ... wallet details
  },
  "amount": 500.00,
  "type": "DEPOSIT",
  "oppositePartyType": "IBAN",
  "oppositeParty": "TR123456789",
  "status": "PENDING",
  "createdAt": "2024-01-01T10:00:00"
}
```

### Step 5: Approve the Transaction
```bash
PUT http://localhost:8080/api/transactions/1/approve
```

**Expected Response:**
```json
{
  "id": 1,
  "status": "APPROVED",
  // ... other fields
}
```

### Step 6: Check Updated Wallet Balance
```bash
GET http://localhost:8080/api/wallets/1
```

**Expected Response:**
```json
{
  "id": 1,
  "balance": 1500.00,  // 1000 + 500
  "usableBalance": 1500.00,
  // ... other fields
}
```

### Step 7: Create a Withdrawal Transaction
```bash
POST http://localhost:8080/api/transactions/withdraw?walletId=1&amount=200.00&oppositePartyType=IBAN&oppositeParty=TR987654321
```

### Step 8: Approve the Withdrawal
```bash
PUT http://localhost:8080/api/transactions/2/approve
```

### Step 9: Check Final Wallet Balance
```bash
GET http://localhost:8080/api/wallets/1
```

**Expected Response:**
```json
{
  "id": 1,
  "balance": 1300.00,  // 1500 - 200
  "usableBalance": 1300.00,
  // ... other fields
}
```

## 🔍 Query Examples

### Get All Transactions for a Wallet
```bash
GET http://localhost:8080/api/transactions/wallet/1
```

### Get Pending Transactions
```bash
GET http://localhost:8080/api/transactions/pending
```

### Get Deposit Transactions Only
```bash
GET http://localhost:8080/api/transactions/wallet/1/deposits
```

### Get Withdrawal Transactions Only
```bash
GET http://localhost:8080/api/transactions/wallet/1/withdrawals
```

## ⚠️ Error Handling Examples

### Insufficient Balance
```bash
POST http://localhost:8080/api/transactions/withdraw?walletId=1&amount=2000.00&oppositePartyType=IBAN&oppositeParty=TR111111111
```

**Expected Response:** `400 Bad Request`

### Duplicate TCKN
```bash
POST http://localhost:8080/api/customers
Content-Type: application/json

{
  "name": "Jane",
  "surname": "Smith", 
  "tckn": "12345678901",  // Same TCKN
  "role": "CUSTOMER"
}
```

**Expected Response:** `400 Bad Request`

### Wallet Not Found
```bash
GET http://localhost:8080/api/wallets/999
```

**Expected Response:** `404 Not Found`

## 🎯 Testing Scenarios

### Scenario 1: Complete Customer Journey
1. Create customer
2. Create wallet
3. Add balance
4. Create deposit transaction
5. Approve transaction
6. Verify balance increase
7. Create withdrawal transaction
8. Approve withdrawal
9. Verify balance decrease

### Scenario 2: Transaction Approval Workflow
1. Create multiple pending transactions
2. List pending transactions
3. Approve some transactions
4. Deny some transactions
5. Verify balance changes only for approved transactions

### Scenario 3: Error Handling
1. Try to create customer with duplicate TCKN
2. Try to withdraw more than available balance
3. Try to approve already approved transaction
4. Try to access non-existent resources

## 📊 Database Verification

### Access H2 Console
1. **Open browser** and go to `http://localhost:8080/h2-console`
2. **Login credentials:**
   - JDBC URL: `jdbc:h2:mem:walletdb`
   - Username: `sa`
   - Password: `password`
3. **Test the connection**
4. **View tables:**
   - `CUSTOMERS`
   - `WALLETS`
   - `TRANSACTIONS`

### Sample Queries
```sql
-- View all customers
SELECT * FROM CUSTOMERS;

-- View all wallets with customer info
SELECT w.*, c.name, c.surname 
FROM WALLETS w 
JOIN CUSTOMERS c ON w.customer_id = c.id;

-- View all transactions with wallet info
SELECT t.*, w.wallet_name, c.name 
FROM TRANSACTIONS t 
JOIN WALLETS w ON t.wallet_id = w.id 
JOIN CUSTOMERS c ON w.customer_id = c.id;
```

## 🎉 Success Criteria

✅ **All endpoints return correct HTTP status codes**
✅ **CRUD operations work correctly**
✅ **Business logic validation works**
✅ **Transaction approval affects wallet balance**
✅ **Error handling provides meaningful messages**
✅ **Database operations are atomic**
✅ **API responses are properly formatted**

## 🚀 Next Steps

After successful testing, you can:
1. **Implement authentication/authorization**
2. **Add input validation**
3. **Implement rate limiting**
4. **Add comprehensive logging**
5. **Create integration tests**
6. **Deploy to production environment**

Happy Testing! 🎯 