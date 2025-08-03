package com.digitalwallet.api.repository;

import com.digitalwallet.api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find transactions by wallet ID
     */
    List<Transaction> findByWalletId(Long walletId);
    
    /**
     * Find transactions by wallet ID and status
     */
    List<Transaction> findByWalletIdAndStatus(Long walletId, Transaction.TransactionStatus status);
    
    /**
     * Find transactions by status
     */
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    /**
     * Find transactions by type
     */
    List<Transaction> findByType(Transaction.TransactionType type);
    
    /**
     * Find transactions by wallet ID and type
     */
    List<Transaction> findByWalletIdAndType(Long walletId, Transaction.TransactionType type);
    
    /**
     * Find transactions by customer ID (through wallet)
     */
    List<Transaction> findByWalletCustomerId(Long customerId);
} 