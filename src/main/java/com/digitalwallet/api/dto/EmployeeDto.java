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
public class EmployeeDto {
    
    private Long id;
    private String name;
    private String surname;
    private String employeeId;
    private Employee.EmployeeRole role;
    
    public static EmployeeDto fromEntity(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .surname(employee.getSurname())
                .employeeId(employee.getEmployeeId())
                .role(employee.getRole())
                .build();
    }
} 