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
        customer.setRole(Customer.UserRole.CUSTOMER);
        
        assertNotNull(customer);
        assertEquals("John", customer.getName());
        assertEquals("Doe", customer.getSurname());
        assertEquals("12345678901", customer.getTckn());
        assertEquals(Customer.UserRole.CUSTOMER, customer.getRole());
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
} 