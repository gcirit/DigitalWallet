package com.digitalwallet.api.dto;

import com.digitalwallet.api.entity.Customer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    
    private Long id;
    private String name;
    private String surname;
    private String tckn;
    // Note: password is not exposed in DTO for security reasons
    
    // Convert from Entity to DTO
    public static CustomerDto fromEntity(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .tckn(customer.getTckn())
                .build();
    }
    
    // Convert from DTO to Entity
    public Customer toEntity() {
        Customer customer = new Customer();
        customer.setId(this.id);
        customer.setName(this.name);
        customer.setSurname(this.surname);
        customer.setTckn(this.tckn);
        // Note: password should be set separately for security
        return customer;
    }
} 