package com.digitalwallet.api.security;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.repository.CustomerRepository;
import com.digitalwallet.api.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthenticationTest {

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Customer testCustomer;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        // Create a test customer with a unique TCKN
        testCustomer = new Customer();
        testCustomer.setName("Test");
        testCustomer.setSurname("User");
        testCustomer.setTckn("11111111111"); // Different TCKN to avoid conflict with DataInitializer
        testCustomer.setPassword("password");
        customerRepository.save(testCustomer);

        // Create a test employee with a unique Employee ID
        testEmployee = new Employee();
        testEmployee.setName("Test");
        testEmployee.setSurname("Employee");
        testEmployee.setEmployeeId("EMP111"); // Different Employee ID to avoid conflict
        testEmployee.setPassword("password");
        testEmployee.setRole(Employee.EmployeeRole.EMPLOYEE);
        employeeRepository.save(testEmployee);
    }

    @Test
    void testSuccessfulCustomerAuthentication() {
        // Create authentication token for customer
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken("11111111111", "password");

        // Authenticate
        Authentication result = authenticationProvider.authenticate(authToken);

        // Verify authentication
        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("11111111111", result.getName());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_CUSTOMER")));
    }

    @Test
    void testSuccessfulEmployeeAuthentication() {
        // Create authentication token for employee
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken("EMP111", "password");

        // Authenticate
        Authentication result = authenticationProvider.authenticate(authToken);

        // Verify authentication
        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("EMP111", result.getName());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_EMPLOYEE")));
    }

    @Test
    void testFailedAuthentication() {
        // Create authentication token with non-existent username
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken("99999999999", "password");

        // Verify authentication fails
        assertThrows(BadCredentialsException.class, () -> {
            authenticationProvider.authenticate(authToken);
        });
    }

    @Test
    void testSupportsMethod() {
        // Test that the provider supports UsernamePasswordAuthenticationToken
        assertTrue(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }
} 