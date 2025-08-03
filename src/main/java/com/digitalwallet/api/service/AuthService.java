package com.digitalwallet.api.service;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.repository.CustomerRepository;
import com.digitalwallet.api.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public Customer getCurrentCustomer() {
        String username = getCurrentUsername();
        if (username != null) {
            return customerRepository.findByTckn(username).orElse(null);
        }
        return null;
    }

    public Employee getCurrentEmployee() {
        String username = getCurrentUsername();
        if (username != null) {
            return employeeRepository.findByEmployeeId(username).orElse(null);
        }
        return null;
    }

    public boolean isCustomer() {
        return getCurrentCustomer() != null;
    }

    public boolean isEmployee() {
        return getCurrentEmployee() != null;
    }

    public boolean isEmployeeOrAdmin() {
        Employee employee = getCurrentEmployee();
        return employee != null && (employee.getRole() == Employee.EmployeeRole.EMPLOYEE || 
                                   employee.getRole() == Employee.EmployeeRole.ADMIN);
    }

    // For backward compatibility
    public String getCurrentUserTckn() {
        return getCurrentUsername();
    }

    public Customer getCurrentUser() {
        return getCurrentCustomer();
    }
} 