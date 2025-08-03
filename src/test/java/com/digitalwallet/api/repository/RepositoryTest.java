package com.digitalwallet.api.repository;

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
class RepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testCustomerRepository() {
        // Create a customer with unique TCKN
        Customer customer = new Customer();
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setTckn("33333333333"); // Unique TCKN to avoid conflict
        customer.setPassword("password");

        // Save customer
        Customer savedCustomer = customerRepository.save(customer);
        assertNotNull(savedCustomer.getId());

        // Find by TCKN
        Customer foundCustomer = customerRepository.findByTckn("33333333333").orElse(null);
        assertNotNull(foundCustomer);
        assertEquals("John", foundCustomer.getName());
    }

    @Test
    void testWalletRepository() {
        // Create a customer first with unique TCKN
        Customer customer = new Customer();
        customer.setName("Jane");
        customer.setSurname("Smith");
        customer.setTckn("44444444444"); // Unique TCKN to avoid conflict
        customer.setPassword("password");
        Customer savedCustomer = customerRepository.save(customer);

        // Create a wallet
        Wallet wallet = new Wallet();
        wallet.setCustomer(savedCustomer);
        wallet.setWalletName("Test Wallet");
        wallet.setCurrency(Wallet.Currency.TRY);
        wallet.setActiveForShopping(true);
        wallet.setActiveForWithdraw(true);

        // Save wallet
        Wallet savedWallet = walletRepository.save(wallet);
        assertNotNull(savedWallet.getId());

        // Find by customer ID
        List<Wallet> customerWallets = walletRepository.findByCustomerId(savedCustomer.getId());
        assertEquals(1, customerWallets.size());
        assertEquals("Test Wallet", customerWallets.get(0).getWalletName());
    }

    @Test
    void testTransactionRepository() {
        // Create a customer and wallet first with unique TCKN
        Customer customer = new Customer();
        customer.setName("Alice");
        customer.setSurname("Johnson");
        customer.setTckn("55555555555"); // Unique TCKN to avoid conflict
        customer.setPassword("password");
        Customer savedCustomer = customerRepository.save(customer);

        Wallet wallet = new Wallet();
        wallet.setCustomer(savedCustomer);
        wallet.setWalletName("Test Wallet");
        wallet.setCurrency(Wallet.Currency.TRY);
        Wallet savedWallet = walletRepository.save(wallet);

        // Create a transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(savedWallet);
        transaction.setAmount(BigDecimal.valueOf(100.00));
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setOppositePartyType(Transaction.OppositePartyType.IBAN);
        transaction.setOppositeParty("TR123456789");
        transaction.setStatus(Transaction.TransactionStatus.PENDING);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        assertNotNull(savedTransaction.getId());

        // Find by wallet ID
        List<Transaction> walletTransactions = transactionRepository.findByWalletId(savedWallet.getId());
        assertEquals(1, walletTransactions.size());
        assertEquals(Transaction.TransactionType.DEPOSIT, walletTransactions.get(0).getType());
    }
} 