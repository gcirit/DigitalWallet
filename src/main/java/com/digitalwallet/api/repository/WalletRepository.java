package com.digitalwallet.api.repository;

import com.digitalwallet.api.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    /**
     * Find wallets by customer ID
     */
    List<Wallet> findByCustomerId(Long customerId);
    
    /**
     * Find wallets by customer ID and currency
     */
    List<Wallet> findByCustomerIdAndCurrency(Long customerId, Wallet.Currency currency);
    
    /**
     * Find wallets by currency
     */
    List<Wallet> findByCurrency(Wallet.Currency currency);
    
    /**
     * Find active wallets for shopping
     */
    List<Wallet> findByActiveForShoppingTrue();
    
    /**
     * Find active wallets for withdrawal
     */
    List<Wallet> findByActiveForWithdrawTrue();
} 