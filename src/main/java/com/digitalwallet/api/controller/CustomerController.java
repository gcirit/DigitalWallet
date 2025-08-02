package com.digitalwallet.api.controller;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Create a new customer
     */
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        log.info("Creating customer: {}", customer.getTckn());
        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (IllegalArgumentException e) {
            log.error("Error creating customer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        log.info("Getting customer by ID: {}", id);
        return customerService.findCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get customer by TCKN
     */
    @GetMapping("/tckn/{tckn}")
    public ResponseEntity<Customer> getCustomerByTckn(@PathVariable String tckn) {
        log.info("Getting customer by TCKN: {}", tckn);
        return customerService.findCustomerByTckn(tckn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all customers
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        log.info("Getting all customers");
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * Get customers by role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Customer>> getCustomersByRole(@PathVariable Customer.UserRole role) {
        log.info("Getting customers by role: {}", role);
        List<Customer> customers = customerService.getCustomersByRole(role);
        return ResponseEntity.ok(customers);
    }

    /**
     * Update customer
     */
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        log.info("Updating customer with ID: {}", id);
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
            return ResponseEntity.ok(updatedCustomer);
        } catch (IllegalArgumentException e) {
            log.error("Error updating customer: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete customer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with ID: {}", id);
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting customer: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if customer exists by TCKN
     */
    @GetMapping("/exists/tckn/{tckn}")
    public ResponseEntity<Boolean> existsByTckn(@PathVariable String tckn) {
        log.info("Checking if customer exists by TCKN: {}", tckn);
        boolean exists = customerService.existsByTckn(tckn);
        return ResponseEntity.ok(exists);
    }
} 