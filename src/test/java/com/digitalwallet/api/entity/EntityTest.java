package com.digitalwallet.api.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void testCustomerEntity() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setTckn("12345678901");
        customer.setPassword("password");
        
        assertNotNull(customer);
        assertEquals("John", customer.getName());
        assertEquals("Doe", customer.getSurname());
        assertEquals("12345678901", customer.getTckn());
        assertEquals("password", customer.getPassword());
    }

    @Test
    void testWalletEntity() {
        Wallet wallet = new Wallet();
        wallet.setWalletName("Test Wallet");
        wallet.setCurrency(Wallet.Currency.TRY);
        wallet.setActiveForShopping(true);
        wallet.setActiveForWithdraw(true);
        
        assertNotNull(wallet);
        assertEquals("Test Wallet", wallet.getWalletName());
        assertEquals(Wallet.Currency.TRY, wallet.getCurrency());
        assertTrue(wallet.isActiveForShopping());
        assertTrue(wallet.isActiveForWithdraw());
    }

    @Test
    void testTransactionEntity() {
        Transaction transaction = new Transaction();
        transaction.setAmount(java.math.BigDecimal.valueOf(100.00));
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setOppositePartyType(Transaction.OppositePartyType.IBAN);
        transaction.setOppositeParty("TR123456789");
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        assertNotNull(transaction);
        assertEquals(java.math.BigDecimal.valueOf(100.00), transaction.getAmount());
        assertEquals(Transaction.TransactionType.DEPOSIT, transaction.getType());
        assertEquals(Transaction.OppositePartyType.IBAN, transaction.getOppositePartyType());
        assertEquals("TR123456789", transaction.getOppositeParty());
        assertEquals(Transaction.TransactionStatus.PENDING, transaction.getStatus());
    }

    @Test
    void testEmployeeEntity() {
        Employee employee = new Employee();
        employee.setName("Jane");
        employee.setSurname("Smith");
        employee.setEmployeeId("EMP001");
        employee.setPassword("password");
        employee.setRole(Employee.EmployeeRole.EMPLOYEE);
        
        assertNotNull(employee);
        assertEquals("Jane", employee.getName());
        assertEquals("Smith", employee.getSurname());
        assertEquals("EMP001", employee.getEmployeeId());
        assertEquals("password", employee.getPassword());
        assertEquals(Employee.EmployeeRole.EMPLOYEE, employee.getRole());
    }
} 