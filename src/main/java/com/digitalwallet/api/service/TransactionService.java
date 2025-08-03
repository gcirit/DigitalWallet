package com.digitalwallet.api.service;

import com.digitalwallet.api.entity.Transaction;
import com.digitalwallet.api.entity.Wallet;
import com.digitalwallet.api.repository.TransactionRepository;
import com.digitalwallet.api.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;

    /**
     * Create a deposit transaction
     */
    public Transaction createDepositTransaction(Long walletId, BigDecimal amount, 
                                            Transaction.OppositePartyType oppositePartyType, 
                                            String oppositeParty) {
        log.info("Creating deposit transaction for wallet ID: {}", walletId);
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));
        
        if (!wallet.isActiveForShopping()) {
            throw new IllegalArgumentException("Wallet is not active for shopping");
        }
        
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setOppositePartyType(oppositePartyType);
        transaction.setOppositeParty(oppositeParty);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Deposit transaction created successfully with ID: {}", savedTransaction.getId());
        return savedTransaction;
    }

    /**
     * Create a withdrawal transaction
     */
    public Transaction createWithdrawTransaction(Long walletId, BigDecimal amount, 
                                             Transaction.OppositePartyType oppositePartyType, 
                                             String oppositeParty) {
        log.info("Creating withdrawal transaction for wallet ID: {}", walletId);
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));
        
        if (!wallet.isActiveForWithdraw()) {
            throw new IllegalArgumentException("Wallet is not active for withdrawal");
        }
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Available: " + wallet.getBalance() + ", Required: " + amount);
        }
        
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.WITHDRAW);
        transaction.setOppositePartyType(oppositePartyType);
        transaction.setOppositeParty(oppositeParty);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Withdrawal transaction created successfully with ID: {}", savedTransaction.getId());
        return savedTransaction;
    }

    /**
     * Approve a transaction
     */
    public Transaction approveTransaction(Long transactionId) {
        log.info("Approving transaction with ID: {}", transactionId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));
        
        if (transaction.getStatus() != Transaction.TransactionStatus.PENDING) {
            throw new IllegalArgumentException("Transaction is not in PENDING status");
        }
        
        transaction.setStatus(Transaction.TransactionStatus.APPROVED);
        
        // Update wallet balance based on transaction type
        if (transaction.getType() == Transaction.TransactionType.DEPOSIT) {
            walletService.addToWalletBalance(transaction.getWallet().getId(), transaction.getAmount());
        } else if (transaction.getType() == Transaction.TransactionType.WITHDRAW) {
            walletService.deductFromWalletBalance(transaction.getWallet().getId(), transaction.getAmount());
        }
        
        Transaction approvedTransaction = transactionRepository.save(transaction);
        log.info("Transaction approved successfully");
        return approvedTransaction;
    }

    /**
     * Deny a transaction
     */
    public Transaction denyTransaction(Long transactionId) {
        log.info("Denying transaction with ID: {}", transactionId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));
        
        if (transaction.getStatus() != Transaction.TransactionStatus.PENDING) {
            throw new IllegalArgumentException("Transaction is not in PENDING status");
        }
        
        transaction.setStatus(Transaction.TransactionStatus.DENIED);
        
        Transaction deniedTransaction = transactionRepository.save(transaction);
        log.info("Transaction denied successfully");
        return deniedTransaction;
    }

    /**
     * Get transaction by ID
     */
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * Get all transactions for a wallet
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByWalletId(Long walletId) {
        return transactionRepository.findByWalletId(walletId);
    }

    /**
     * Get transactions by wallet ID and status
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByWalletIdAndStatus(Long walletId, Transaction.TransactionStatus status) {
        return transactionRepository.findByWalletIdAndStatus(walletId, status);
    }

    /**
     * Get all transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Get transactions by status
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByStatus(Transaction.TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }

    /**
     * Get pending transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findByStatus(Transaction.TransactionStatus.PENDING);
    }

    /**
     * Get transactions by type
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByType(Transaction.TransactionType type) {
        return transactionRepository.findByType(type);
    }

    /**
     * Get transactions by wallet ID and type
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByWalletIdAndType(Long walletId, Transaction.TransactionType type) {
        return transactionRepository.findByWalletIdAndType(walletId, type);
    }

    /**
     * Get deposit transactions for a wallet
     */
    @Transactional(readOnly = true)
    public List<Transaction> getDepositTransactionsByWalletId(Long walletId) {
        return transactionRepository.findByWalletIdAndType(walletId, Transaction.TransactionType.DEPOSIT);
    }

    /**
     * Get withdrawal transactions for a wallet
     */
    @Transactional(readOnly = true)
    public List<Transaction> getWithdrawTransactionsByWalletId(Long walletId) {
        return transactionRepository.findByWalletIdAndType(walletId, Transaction.TransactionType.WITHDRAW);
    }

    /**
     * Get all transactions for a customer
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByWalletCustomerId(customerId);
    }
} 