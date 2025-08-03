package com.digitalwallet.api.config;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.repository.CustomerRepository;
import com.digitalwallet.api.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create test data if they don't exist
        if (customerRepository.count() == 0 && employeeRepository.count() == 0) {
            log.info("Initializing test data...");
            
            // Create a test customer
            Customer customer = Customer.builder()
                    .name("John")
                    .surname("Doe")
                    .tckn("12345678901")
                    .password("password")
                    .build();
            customerRepository.save(customer);
            
            // Create a test employee (ADMIN)
            Employee employee = Employee.builder()
                    .name("Jane")
                    .surname("Smith")
                    .employeeId("EMP001")
                    .password("password")
                    .role(Employee.EmployeeRole.ADMIN)
                    .build();
            employeeRepository.save(employee);
            
            log.info("Test data initialized successfully!");
        }
    }
} 