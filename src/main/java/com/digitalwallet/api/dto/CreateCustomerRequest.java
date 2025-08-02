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
public class CreateCustomerRequest {
    
    private String name;
    private String surname;
    private String tckn;
    private Customer.UserRole role;
    
    // Convert to Entity
    public Customer toEntity() {
        Customer customer = new Customer();
        customer.setName(this.name);
        customer.setSurname(this.surname);
        customer.setTckn(this.tckn);
        customer.setRole(this.role);
        return customer;
    }
} 