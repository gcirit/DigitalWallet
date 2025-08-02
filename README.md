# Digital Wallet

A Spring Boot application for digital wallet operations.

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Security** with JWT
- **Spring Data JPA**
- **H2 Database**
- **Maven**
- **Lombok**
- **JUnit 5** for testing

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Building and Running the Project

### 1. Build the Project
```bash
mvn clean install
```

### 2. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access H2 Database Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:walletdb`
- Username: `sa`
- Password: `password`

## Testing

### Run Tests
```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/digitalwallet/api/
│   │   └── DigitalWalletApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/digitalwallet/api/
    │   └── DigitalWalletApplicationTests.java
    └── resources/
        └── application-test.properties
```

This is a basic Spring Boot project ready for development in IntelliJ IDEA. 