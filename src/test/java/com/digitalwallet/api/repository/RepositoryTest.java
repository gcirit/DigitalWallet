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
        // Create a customer
        Customer customer = new Customer();
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setTckn("12345678901");
        customer.setRole(Customer.UserRole.CUSTOMER);

        // Save customer
        Customer savedCustomer = customerRepository.save(customer);
        assertNotNull(savedCustomer.getId());

        // Find by TCKN
        Customer foundCustomer = customerRepository.findByTckn("12345678901").orElse(null);
        assertNotNull(foundCustomer);
        assertEquals("John", foundCustomer.getName());
    }

    @Test
    void testWalletRepository() {
        // Create a customer first
        Customer customer = new Customer();
        customer.setName("Jane");
        customer.setSurname("Smith");
        customer.setTckn("98765432109");
        customer.setRole(Customer.UserRole.CUSTOMER);
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
        // Create a customer and wallet first
        Customer customer = new Customer();
        customer.setName("Alice");
        customer.setSurname("Johnson");
        customer.setTckn("55566677788");
        customer.setRole(Customer.UserRole.CUSTOMER);
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