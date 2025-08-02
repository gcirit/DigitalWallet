package com.digitalwallet.api.service;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Create a new customer
     */
    public Customer createCustomer(Customer customer) {
        log.info("Creating customer: {}", customer.getTckn());
        
        // Validate TCKN uniqueness
        if (customerRepository.existsByTckn(customer.getTckn())) {
            throw new IllegalArgumentException("Customer with TCKN " + customer.getTckn() + " already exists");
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    /**
     * Find customer by ID
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Find customer by TCKN
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerByTckn(String tckn) {
        return customerRepository.findByTckn(tckn);
    }

    /**
     * Get all customers
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Get customers by role
     */
    @Transactional(readOnly = true)
    public List<Customer> getCustomersByRole(Customer.UserRole role) {
        return customerRepository.findByRole(role);
    }

    /**
     * Update customer
     */
    public Customer updateCustomer(Long id, Customer customerDetails) {
        log.info("Updating customer with ID: {}", id);
        
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        
        // Update fields
        existingCustomer.setName(customerDetails.getName());
        existingCustomer.setSurname(customerDetails.getSurname());
        existingCustomer.setRole(customerDetails.getRole());
        
        // Don't update TCKN as it should remain unique
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("Customer updated successfully");
        return updatedCustomer;
    }

    /**
     * Delete customer
     */
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with ID: " + id);
        }
        
        customerRepository.deleteById(id);
        log.info("Customer deleted successfully");
    }

    /**
     * Check if customer exists by TCKN
     */
    @Transactional(readOnly = true)
    public boolean existsByTckn(String tckn) {
        return customerRepository.existsByTckn(tckn);
    }
} 