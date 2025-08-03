package com.digitalwallet.api.controller;

import com.digitalwallet.api.dto.CustomerDto;
import com.digitalwallet.api.dto.EmployeeDto;
import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Get current user info (supports both Customer and Employee)
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        log.info("Getting current user info");
        try {
            // Check for Customer first
            Customer customer = authService.getCurrentCustomer();
            if (customer != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("type", "CUSTOMER");
                response.put("user", CustomerDto.fromEntity(customer));
                return ResponseEntity.ok(response);
            }
            
            // Check for Employee
            Employee employee = authService.getCurrentEmployee();
            if (employee != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("type", "EMPLOYEE");
                response.put("user", EmployeeDto.fromEntity(employee));
                return ResponseEntity.ok(response);
            }
            
            // No authenticated user found
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        }
    }
} 