package com.digitalwallet.api.repository;

import com.digitalwallet.api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Find customer by TCKN (Turkish Citizenship Number)
     */
    Optional<Customer> findByTckn(String tckn);
    
    /**
     * Check if customer exists by TCKN
     */
    boolean existsByTckn(String tckn);
    
    /**
     * Find customers by role
     */
    // Role-based methods removed since customers don't have roles anymore
} 