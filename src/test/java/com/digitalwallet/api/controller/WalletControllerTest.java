package com.digitalwallet.api.controller;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.entity.Wallet;
import com.digitalwallet.api.repository.CustomerRepository;
import com.digitalwallet.api.repository.EmployeeRepository;
import com.digitalwallet.api.repository.WalletRepository;
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
class WalletControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletService walletService;

    private MockMvc mockMvc;

    private Customer testCustomer;
    private Employee testEmployee;
    private Employee testAdmin;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        // Create test customer with unique TCKN
        testCustomer = new Customer();
        testCustomer.setName("Test");
        testCustomer.setSurname("Customer");
        testCustomer.setTckn("88888888888"); // Unique TCKN to avoid conflicts
        testCustomer.setPassword("password");
        testCustomer = customerRepository.save(testCustomer);

        // Create test employee with unique employee ID
        testEmployee = new Employee();
        testEmployee.setName("Test");
        testEmployee.setSurname("Employee");
        testEmployee.setEmployeeId("EMP888"); // Unique employee ID to avoid conflicts
        testEmployee.setPassword("password");
        testEmployee.setRole(Employee.EmployeeRole.EMPLOYEE);
        testEmployee = employeeRepository.save(testEmployee);

        // Create test admin with unique employee ID
        testAdmin = new Employee();
        testAdmin.setName("Test");
        testAdmin.setSurname("Admin");
        testAdmin.setEmployeeId("EMP887"); // Unique employee ID to avoid conflicts
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
    }

    @Test
    void testCreateWallet_Unauthenticated_ShouldReturn401() throws Exception {
        String walletRequest = "{\"walletName\":\"New Wallet\",\"currency\":\"TRY\",\"activeForShopping\":true,\"activeForWithdraw\":true}";
        
        mockMvc.perform(post("/api/wallets/customer/{customerId}", testCustomer.getId())
                        .content(walletRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllWallets_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetWalletsByCustomerId_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/wallets/customer/{customerId}", testCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetWalletById_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/wallets/{id}", testWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAddToWalletBalance_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/wallets/{id}/balance/add", testWallet.getId())
                        .content("25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeductFromWalletBalance_Unauthenticated_ShouldReturn401() throws Exception {
        // First add some balance
        walletService.addToWalletBalance(testWallet.getId(), BigDecimal.valueOf(100));
        
        mockMvc.perform(post("/api/wallets/{id}/balance/deduct", testWallet.getId())
                        .content("25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateWalletBalance_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(put("/api/wallets/{id}/balance", testWallet.getId())
                        .content("100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateWalletStatus_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(put("/api/wallets/{id}/status", testWallet.getId())
                        .param("activeForShopping", "true")
                        .param("activeForWithdraw", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteWallet_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/wallets/{id}", testWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetWalletsByCurrency_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/wallets/currency/TRY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetActiveWalletsForShopping_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/wallets/active/shopping")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetActiveWalletsForWithdraw_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/wallets/active/withdraw")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetWalletsByCustomerIdAndCurrency_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/wallets/customer/{customerId}/currency/TRY", testCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
} 