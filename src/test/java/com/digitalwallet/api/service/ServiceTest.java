package com.digitalwallet.api.service;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Wallet;
import com.digitalwallet.api.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Test
    void testCustomerService() {
        // Create customer with unique TCKN
        Customer customer = new Customer();
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setTckn("66666666666"); // Unique TCKN to avoid conflict
        customer.setPassword("password");

        Customer savedCustomer = customerService.createCustomer(customer);
        assertNotNull(savedCustomer.getId());
        assertEquals("John", savedCustomer.getName());

        // Test duplicate TCKN
        Customer duplicateCustomer = new Customer();
        duplicateCustomer.setName("Jane");
        duplicateCustomer.setSurname("Smith");
        duplicateCustomer.setTckn("66666666666"); // Same TCKN for duplicate test
        duplicateCustomer.setPassword("password");

        assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer(duplicateCustomer);
        });
    }

    @Test
    void testWalletService() {
        // Create customer first with unique TCKN
        Customer customer = new Customer();
        customer.setName("Alice");
        customer.setSurname("Johnson");
        customer.setTckn("77777777777"); // Unique TCKN to avoid conflict
        customer.setPassword("password");
        Customer savedCustomer = customerService.createCustomer(customer);

        // Create wallet
        Wallet wallet = new Wallet();
        wallet.setWalletName("Test Wallet");
        wallet.setCurrency(Wallet.Currency.TRY);
        wallet.setActiveForShopping(true);
        wallet.setActiveForWithdraw(true);

        Wallet savedWallet = walletService.createWallet(savedCustomer.getId(), wallet);
        assertNotNull(savedWallet.getId());
        assertEquals(BigDecimal.ZERO, savedWallet.getBalance());

        // Test balance operations
        Wallet updatedWallet = walletService.addToWalletBalance(savedWallet.getId(), BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), updatedWallet.getBalance());

        Wallet deductedWallet = walletService.deductFromWalletBalance(savedWallet.getId(), BigDecimal.valueOf(50));
        assertEquals(BigDecimal.valueOf(50), deductedWallet.getBalance());

        // Test insufficient balance
        assertThrows(IllegalArgumentException.class, () -> {
            walletService.deductFromWalletBalance(savedWallet.getId(), BigDecimal.valueOf(100));
        });
    }

    @Test
    void testTransactionService() {
        // Create customer and wallet with unique TCKN
        Customer customer = new Customer();
        customer.setName("Bob");
        customer.setSurname("Wilson");
        customer.setTckn("88888888888"); // Unique TCKN to avoid conflict
        customer.setPassword("password");
        Customer savedCustomer = customerService.createCustomer(customer);

        Wallet wallet = new Wallet();
        wallet.setWalletName("Test Wallet");
        wallet.setCurrency(Wallet.Currency.TRY);
        wallet.setActiveForShopping(true);
        wallet.setActiveForWithdraw(true);
        Wallet savedWallet = walletService.createWallet(savedCustomer.getId(), wallet);

        // Add some balance
        walletService.addToWalletBalance(savedWallet.getId(), BigDecimal.valueOf(200));

        // Test deposit transaction
        Transaction depositTransaction = transactionService.createDepositTransaction(
                savedWallet.getId(),
                BigDecimal.valueOf(50),
                Transaction.OppositePartyType.IBAN,
                "TR123456789"
        );
        assertNotNull(depositTransaction.getId());
        assertEquals(Transaction.TransactionStatus.PENDING, depositTransaction.getStatus());

        // Test withdrawal transaction
        Transaction withdrawTransaction = transactionService.createWithdrawTransaction(
                savedWallet.getId(),
                BigDecimal.valueOf(30),
                Transaction.OppositePartyType.IBAN,
                "TR987654321"
        );
        assertNotNull(withdrawTransaction.getId());
        assertEquals(Transaction.TransactionStatus.PENDING, withdrawTransaction.getStatus());

        // Test insufficient balance for withdrawal
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createWithdrawTransaction(
                    savedWallet.getId(),
                    BigDecimal.valueOf(300), // More than available balance
                    Transaction.OppositePartyType.IBAN,
                    "TR111111111"
            );
        });
    }

    @Test
    void testTransactionApproval() {
        // Create customer and wallet with unique TCKN
        Customer customer = new Customer();
        customer.setName("Charlie");
        customer.setSurname("Brown");
        customer.setTckn("99999999999"); // Unique TCKN to avoid conflict
        customer.setPassword("password");
        Customer savedCustomer = customerService.createCustomer(customer);

        Wallet wallet = new Wallet();
        wallet.setWalletName("Test Wallet");
        wallet.setCurrency(Wallet.Currency.TRY);
        wallet.setActiveForShopping(true);
        wallet.setActiveForWithdraw(true);
        Wallet savedWallet = walletService.createWallet(savedCustomer.getId(), wallet);

        // Add initial balance
        walletService.addToWalletBalance(savedWallet.getId(), BigDecimal.valueOf(100));

        // Create deposit transaction
        Transaction depositTransaction = transactionService.createDepositTransaction(
                savedWallet.getId(),
                BigDecimal.valueOf(50),
                Transaction.OppositePartyType.IBAN,
                "TR123456789"
        );

        // Approve deposit transaction
        Transaction approvedDeposit = transactionService.approveTransaction(depositTransaction.getId());
        assertEquals(Transaction.TransactionStatus.APPROVED, approvedDeposit.getStatus());

        // Check wallet balance increased
        Wallet updatedWallet = walletService.getWalletById(savedWallet.getId()).orElse(null);
        assertNotNull(updatedWallet);
        assertEquals(BigDecimal.valueOf(150), updatedWallet.getBalance());

        // Create withdrawal transaction
        Transaction withdrawTransaction = transactionService.createWithdrawTransaction(
                savedWallet.getId(),
                BigDecimal.valueOf(30),
                Transaction.OppositePartyType.IBAN,
                "TR987654321"
        );

        // Approve withdrawal transaction
        Transaction approvedWithdraw = transactionService.approveTransaction(withdrawTransaction.getId());
        assertEquals(Transaction.TransactionStatus.APPROVED, approvedWithdraw.getStatus());

        // Check wallet balance decreased
        Wallet finalWallet = walletService.getWalletById(savedWallet.getId()).orElse(null);
        assertNotNull(finalWallet);
        assertEquals(BigDecimal.valueOf(120), finalWallet.getBalance());
    }

    @Test
    void testTransactionDenial() {
        // Create customer and wallet with unique TCKN
        Customer customer = new Customer();
        customer.setName("David");
        customer.setSurname("Miller");
        customer.setTckn("10101010101"); // Unique TCKN to avoid conflict
        customer.setPassword("password");
        Customer savedCustomer = customerService.createCustomer(customer);

        Wallet wallet = new Wallet();
        wallet.setWalletName("Test Wallet");
        wallet.setCurrency(Wallet.Currency.TRY);
        wallet.setActiveForShopping(true);
        wallet.setActiveForWithdraw(true);
        Wallet savedWallet = walletService.createWallet(savedCustomer.getId(), wallet);

        // Add initial balance
        walletService.addToWalletBalance(savedWallet.getId(), BigDecimal.valueOf(100));

        // Create deposit transaction
        Transaction depositTransaction = transactionService.createDepositTransaction(
                savedWallet.getId(),
                BigDecimal.valueOf(50),
                Transaction.OppositePartyType.IBAN,
                "TR123456789"
        );

        // Deny deposit transaction
        Transaction deniedDeposit = transactionService.denyTransaction(depositTransaction.getId());
        assertEquals(Transaction.TransactionStatus.DENIED, deniedDeposit.getStatus());

        // Check wallet balance unchanged
        Wallet updatedWallet = walletService.getWalletById(savedWallet.getId()).orElse(null);
        assertNotNull(updatedWallet);
        assertEquals(BigDecimal.valueOf(100), updatedWallet.getBalance());
    }

    @Test
    void testQueryMethods() {
        // Create customer and wallet with unique TCKN
        Customer customer = new Customer();
        customer.setName("Eve");
        customer.setSurname("Anderson");
        customer.setTckn("12121212121"); // Unique TCKN to avoid conflict
        customer.setPassword("password");
        Customer savedCustomer = customerService.createCustomer(customer);

        Wallet wallet = new Wallet();
        wallet.setWalletName("Test Wallet");
        wallet.setCurrency(Wallet.Currency.TRY);
        wallet.setActiveForShopping(true);
        wallet.setActiveForWithdraw(true);
        Wallet savedWallet = walletService.createWallet(savedCustomer.getId(), wallet);

        // Add some balance to the wallet first
        walletService.addToWalletBalance(savedWallet.getId(), BigDecimal.valueOf(200));

        // Create multiple transactions
        transactionService.createDepositTransaction(savedWallet.getId(), BigDecimal.valueOf(100), 
                Transaction.OppositePartyType.IBAN, "TR111111111");
        transactionService.createWithdrawTransaction(savedWallet.getId(), BigDecimal.valueOf(50), 
                Transaction.OppositePartyType.IBAN, "TR222222222");
        transactionService.createDepositTransaction(savedWallet.getId(), BigDecimal.valueOf(75), 
                Transaction.OppositePartyType.PAYMENT, "PAYMENT123");

        // Test query methods
        List<Transaction> allTransactions = transactionService.getTransactionsByWalletId(savedWallet.getId());
        assertEquals(3, allTransactions.size());

        List<Transaction> depositTransactions = transactionService.getDepositTransactionsByWalletId(savedWallet.getId());
        assertEquals(2, depositTransactions.size());

        List<Transaction> withdrawTransactions = transactionService.getWithdrawTransactionsByWalletId(savedWallet.getId());
        assertEquals(1, withdrawTransactions.size());

        List<Transaction> pendingTransactions = transactionService.getPendingTransactions();
        assertEquals(3, pendingTransactions.size());
    }
} 