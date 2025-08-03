package com.digitalwallet.api.controller;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.entity.Transaction;
import com.digitalwallet.api.entity.Wallet;
import com.digitalwallet.api.repository.CustomerRepository;
import com.digitalwallet.api.repository.EmployeeRepository;
import com.digitalwallet.api.repository.TransactionRepository;
import com.digitalwallet.api.repository.WalletRepository;
import com.digitalwallet.api.service.TransactionService;
import com.digitalwallet.api.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class TransactionControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    private MockMvc mockMvc;

    private Customer testCustomer;
    private Employee testEmployee;
    private Employee testAdmin;
    private Wallet testWallet;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        // Create test customer with unique TCKN
        testCustomer = new Customer();
        testCustomer.setName("Test");
        testCustomer.setSurname("Customer");
        testCustomer.setTckn("99999999999"); // Unique TCKN to avoid conflicts
        testCustomer.setPassword("password");
        testCustomer = customerRepository.save(testCustomer);

        // Create test employee with unique employee ID
        testEmployee = new Employee();
        testEmployee.setName("Test");
        testEmployee.setSurname("Employee");
        testEmployee.setEmployeeId("EMP999"); // Unique employee ID to avoid conflicts
        testEmployee.setPassword("password");
        testEmployee.setRole(Employee.EmployeeRole.EMPLOYEE);
        testEmployee = employeeRepository.save(testEmployee);

        // Create test admin with unique employee ID
        testAdmin = new Employee();
        testAdmin.setName("Test");
        testAdmin.setSurname("Admin");
        testAdmin.setEmployeeId("EMP998"); // Unique employee ID to avoid conflicts
        testAdmin.setPassword("password");
        testAdmin.setRole(Employee.EmployeeRole.ADMIN);
        testAdmin = employeeRepository.save(testAdmin);

        // Create test wallet
        testWallet = new Wallet();
        testWallet.setWalletName("Test Wallet");
        testWallet.setCurrency(Wallet.Currency.TRY);
        testWallet.setActiveForShopping(true);
        testWallet.setActiveForWithdraw(true);
        testWallet.setCustomer(testCustomer);
        testWallet = walletRepository.save(testWallet);

        // Add balance to wallet
        walletService.addToWalletBalance(testWallet.getId(), BigDecimal.valueOf(100));

        // Create test transaction
        testTransaction = transactionService.createDepositTransaction(
                testWallet.getId(),
                BigDecimal.valueOf(50),
                Transaction.OppositePartyType.IBAN,
                "TR123456789"
        );
    }

    @Test
    void testCreateDepositTransaction_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/transactions/deposit")
                        .param("walletId", testWallet.getId().toString())
                        .param("amount", "25")
                        .param("oppositePartyType", "IBAN")
                        .param("oppositeParty", "TR999999999")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testApproveTransaction_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(put("/api/transactions/{id}/approve", testTransaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDenyTransaction_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(put("/api/transactions/{id}/deny", testTransaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllTransactions_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetPendingTransactions_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions/pending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTransactionsByWalletId_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions/wallet/{walletId}", testWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTransactionById_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions/{id}", testTransaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateWithdrawTransaction_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/transactions/withdraw")
                        .param("walletId", testWallet.getId().toString())
                        .param("amount", "25")
                        .param("oppositePartyType", "IBAN")
                        .param("oppositeParty", "TR999999999")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTransactionsByStatus_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions/status/PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTransactionsByType_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions/type/DEPOSIT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTransactionsByWalletIdAndStatus_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions/wallet/{walletId}/status/PENDING", testWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTransactionsByWalletIdAndType_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions/wallet/{walletId}/type/DEPOSIT", testWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetDepositTransactionsByWalletId_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions/wallet/{walletId}/deposits", testWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetWithdrawTransactionsByWalletId_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/transactions/wallet/{walletId}/withdrawals", testWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
} 