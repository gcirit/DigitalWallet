package com.digitalwallet.api.repository;

import com.digitalwallet.api.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimpleRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testContextLoads() {
        assertNotNull(customerRepository);
    }

    @Test
    void testSaveAndFindCustomer() {
        // Create a customer
        Customer customer = new Customer();
        customer.setName("Test");
        customer.setSurname("User");
        customer.setTckn("12345678901");
        customer.setRole(Customer.UserRole.CUSTOMER);

        // Save customer
        Customer savedCustomer = customerRepository.save(customer);
        assertNotNull(savedCustomer.getId());
        assertEquals("Test", savedCustomer.getName());

        // Find by ID
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNotNull(foundCustomer);
        assertEquals("Test", foundCustomer.getName());
    }
} 