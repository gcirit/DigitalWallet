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
public class UpdateCustomerRequest {
    
    private String name;
    private String surname;
    private String password;
    
    // Convert to Entity (without TCKN since it shouldn't change)
    public Customer toEntity() {
        Customer customer = new Customer();
        customer.setName(this.name);
        customer.setSurname(this.surname);
        customer.setPassword(this.password);
        // Note: TCKN is not set as it should not be changed during updates
        return customer;
    }
} 