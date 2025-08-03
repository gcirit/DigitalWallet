package com.digitalwallet.api.repository;

import com.digitalwallet.api.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmployeeId(String employeeId);
    
    boolean existsByEmployeeId(String employeeId);
    
    List<Employee> findByRole(Employee.EmployeeRole role);
} 