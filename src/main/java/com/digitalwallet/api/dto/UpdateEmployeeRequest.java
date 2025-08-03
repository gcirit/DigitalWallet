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
public class UpdateEmployeeRequest {
    
    private String name;
    private String surname;
    private String password;
    private Employee.EmployeeRole role;
    
    // Convert to Entity (without employeeId since it shouldn't change)
    public Employee toEntity() {
        Employee employee = new Employee();
        employee.setName(this.name);
        employee.setSurname(this.surname);
        employee.setPassword(this.password);
        employee.setRole(this.role);
        // Note: employeeId is not set as it should not be changed during updates
        return employee;
    }
} 