package com.digitalwallet.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String surname;
    
    @Column(nullable = false, unique = true)
    private String employeeId; // Unique employee identifier
    
    @Column(nullable = false)
    private String password; // In production, this should be hashed
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole role = EmployeeRole.EMPLOYEE;
    
    public enum EmployeeRole {
        EMPLOYEE, MANAGER, ADMIN
    }
} 