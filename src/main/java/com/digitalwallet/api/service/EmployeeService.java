package com.digitalwallet.api.service;

import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee createEmployee(Employee employee) {
        log.info("Creating employee: {}", employee.getEmployeeId());
        
        if (employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
            throw new IllegalArgumentException("Employee with ID " + employee.getEmployeeId() + " already exists");
        }
        
        return employeeRepository.save(employee);
    }

    public Optional<Employee> findEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> findEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeesByRole(Employee.EmployeeRole role) {
        return employeeRepository.findByRole(role);
    }

    public Employee updateEmployee(Long id, Employee employeeDetails) {
        log.info("Updating employee with ID: {}", id);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));
        
        // Note: employeeId validation is not needed since we don't update it
        
        employee.setName(employeeDetails.getName());
        employee.setSurname(employeeDetails.getSurname());
        // Note: employeeId is not updated to prevent ID changes
        employee.setPassword(employeeDetails.getPassword());
        employee.setRole(employeeDetails.getRole());
        
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);
        
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee not found with ID: " + id);
        }
        
        employeeRepository.deleteById(id);
    }

    public boolean existsByEmployeeId(String employeeId) {
        return employeeRepository.existsByEmployeeId(employeeId);
    }
} 