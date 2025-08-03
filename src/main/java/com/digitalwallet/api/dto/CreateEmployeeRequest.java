package com.digitalwallet.api.dto;

import com.digitalwallet.api.entity.Employee;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployeeRequest {
    
    private String name;
    private String surname;
    private String employeeId;
    private String password;
    private Employee.EmployeeRole role;
    
    public Employee toEntity() {
        return Employee.builder()
                .name(name)
                .surname(surname)
                .employeeId(employeeId)
                .password(password)
                .role(role != null ? role : Employee.EmployeeRole.EMPLOYEE)
                .build();
    }
} 