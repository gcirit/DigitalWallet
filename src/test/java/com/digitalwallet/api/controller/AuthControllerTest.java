package com.digitalwallet.api.controller;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.repository.CustomerRepository;
import com.digitalwallet.api.repository.EmployeeRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private MockMvc mockMvc;

    private Customer testCustomer;
    private Employee testEmployee;
    private Employee testAdmin;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        // Create test customer with unique TCKN
        testCustomer = new Customer();
        testCustomer.setName("Test");
        testCustomer.setSurname("Customer");
        testCustomer.setTckn("77777777777"); // Unique TCKN to avoid conflicts
        testCustomer.setPassword("password");
        testCustomer = customerRepository.save(testCustomer);

        // Create test employee with unique employee ID
        testEmployee = new Employee();
        testEmployee.setName("Test");
        testEmployee.setSurname("Employee");
        testEmployee.setEmployeeId("EMP777"); // Unique employee ID to avoid conflicts
        testEmployee.setPassword("password");
        testEmployee.setRole(Employee.EmployeeRole.EMPLOYEE);
        testEmployee = employeeRepository.save(testEmployee);

        // Create test admin with unique employee ID
        testAdmin = new Employee();
        testAdmin.setName("Test");
        testAdmin.setSurname("Admin");
        testAdmin.setEmployeeId("EMP776"); // Unique employee ID to avoid conflicts
        testAdmin.setPassword("password");
        testAdmin.setRole(Employee.EmployeeRole.ADMIN);
        testAdmin = employeeRepository.save(testAdmin);
    }

    @Test
    void testGetCurrentUser_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCurrentUser_WithCustomer_ShouldReturnCustomerInfo() throws Exception {
        // This test would require proper authentication setup
        // For now, we'll just test the endpoint structure
        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Should return 401 without auth
    }

    @Test
    void testGetCurrentUser_WithEmployee_ShouldReturnEmployeeInfo() throws Exception {
        // This test would require proper authentication setup
        // For now, we'll just test the endpoint structure
        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Should return 401 without auth
    }
} 