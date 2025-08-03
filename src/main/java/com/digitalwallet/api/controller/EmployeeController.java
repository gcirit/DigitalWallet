package com.digitalwallet.api.controller;

import com.digitalwallet.api.dto.EmployeeDto;
import com.digitalwallet.api.dto.CreateEmployeeRequest;
import com.digitalwallet.api.dto.UpdateEmployeeRequest;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.service.AuthService;
import com.digitalwallet.api.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AuthService authService;

    /**
     * Create a new employee (ADMIN only)
     */
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody CreateEmployeeRequest request) {
        log.info("Creating employee: {}", request.getEmployeeId());
        try {
            // Check authorization - only ADMIN can create employees
            if (!authService.isEmployee()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Employee currentEmployee = authService.getCurrentEmployee();
            if (currentEmployee == null || currentEmployee.getRole() != Employee.EmployeeRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Employee employee = request.toEntity();
            Employee createdEmployee = employeeService.createEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(EmployeeDto.fromEntity(createdEmployee));
        } catch (IllegalArgumentException e) {
            log.error("Error creating employee: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get employee by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        log.info("Getting employee by ID: {}", id);
        return employeeService.findEmployeeById(id)
                .map(EmployeeDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get employee by Employee ID
     */
    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        log.info("Getting employee by Employee ID: {}", employeeId);
        return employeeService.findEmployeeByEmployeeId(employeeId)
                .map(EmployeeDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all employees (ADMIN only)
     */
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("Getting all employees");
        
        // Check authorization - only ADMIN can view all employees
        if (!authService.isEmployee()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Employee currentEmployee = authService.getCurrentEmployee();
        if (currentEmployee == null || currentEmployee.getRole() != Employee.EmployeeRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Employee> employees = employeeService.getAllEmployees();
        List<EmployeeDto> employeeDtos = employees.stream()
                .map(EmployeeDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employeeDtos);
    }

    /**
     * Get employees by role (ADMIN only)
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByRole(@PathVariable Employee.EmployeeRole role) {
        log.info("Getting employees by role: {}", role);
        
        // Check authorization - only ADMIN can view employees by role
        if (!authService.isEmployee()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Employee currentEmployee = authService.getCurrentEmployee();
        if (currentEmployee == null || currentEmployee.getRole() != Employee.EmployeeRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Employee> employees = employeeService.getEmployeesByRole(role);
        List<EmployeeDto> employeeDtos = employees.stream()
                .map(EmployeeDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employeeDtos);
    }

    /**
     * Update employee (ADMIN only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeRequest request) {
        log.info("Updating employee with ID: {}", id);
        try {
            // Check authorization - only ADMIN can update employees
            if (!authService.isEmployee()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Employee currentEmployee = authService.getCurrentEmployee();
            if (currentEmployee == null || currentEmployee.getRole() != Employee.EmployeeRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Employee employeeDetails = request.toEntity();
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
            return ResponseEntity.ok(EmployeeDto.fromEntity(updatedEmployee));
        } catch (IllegalArgumentException e) {
            log.error("Error updating employee: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete employee (ADMIN only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("Deleting employee with ID: {}", id);
        try {
            // Check authorization - only ADMIN can delete employees
            if (!authService.isEmployee()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Employee currentEmployee = authService.getCurrentEmployee();
            if (currentEmployee == null || currentEmployee.getRole() != Employee.EmployeeRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            employeeService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting employee: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if employee exists by Employee ID
     */
    @GetMapping("/exists/employee-id/{employeeId}")
    public ResponseEntity<Boolean> existsByEmployeeId(@PathVariable String employeeId) {
        log.info("Checking if employee exists by Employee ID: {}", employeeId);
        boolean exists = employeeService.existsByEmployeeId(employeeId);
        return ResponseEntity.ok(exists);
    }
} 