package com.digitalwallet.api.controller;

import com.digitalwallet.api.dto.TransactionDto;
import com.digitalwallet.api.entity.Transaction;
import com.digitalwallet.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Create a deposit transaction
     */
    @PostMapping("/deposit")
    public ResponseEntity<TransactionDto> createDepositTransaction(
            @RequestParam Long walletId,
            @RequestParam BigDecimal amount,
            @RequestParam Transaction.OppositePartyType oppositePartyType,
            @RequestParam String oppositeParty) {
        log.info("Creating deposit transaction for wallet ID: {}", walletId);
        try {
            Transaction transaction = transactionService.createDepositTransaction(walletId, amount, oppositePartyType, oppositeParty);
            return ResponseEntity.status(HttpStatus.CREATED).body(TransactionDto.fromEntity(transaction));
        } catch (IllegalArgumentException e) {
            log.error("Error creating deposit transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a withdrawal transaction
     */
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDto> createWithdrawTransaction(
            @RequestParam Long walletId,
            @RequestParam BigDecimal amount,
            @RequestParam Transaction.OppositePartyType oppositePartyType,
            @RequestParam String oppositeParty) {
        log.info("Creating withdrawal transaction for wallet ID: {}", walletId);
        try {
            Transaction transaction = transactionService.createWithdrawTransaction(walletId, amount, oppositePartyType, oppositeParty);
            return ResponseEntity.status(HttpStatus.CREATED).body(TransactionDto.fromEntity(transaction));
        } catch (IllegalArgumentException e) {
            log.error("Error creating withdrawal transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Approve a transaction
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<TransactionDto> approveTransaction(@PathVariable Long id) {
        log.info("Approving transaction with ID: {}", id);
        try {
            Transaction approvedTransaction = transactionService.approveTransaction(id);
            return ResponseEntity.ok(TransactionDto.fromEntity(approvedTransaction));
        } catch (IllegalArgumentException e) {
            log.error("Error approving transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deny a transaction
     */
    @PutMapping("/{id}/deny")
    public ResponseEntity<TransactionDto> denyTransaction(@PathVariable Long id) {
        log.info("Denying transaction with ID: {}", id);
        try {
            Transaction deniedTransaction = transactionService.denyTransaction(id);
            return ResponseEntity.ok(TransactionDto.fromEntity(deniedTransaction));
        } catch (IllegalArgumentException e) {
            log.error("Error denying transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get transaction by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        log.info("Getting transaction by ID: {}", id);
        return transactionService.getTransactionById(id)
                .map(TransactionDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all transactions for a wallet
     */
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByWalletId(@PathVariable Long walletId) {
        log.info("Getting transactions for wallet ID: {}", walletId);
        List<Transaction> transactions = transactionService.getTransactionsByWalletId(walletId);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    /**
     * Get transactions by wallet ID and status
     */
    @GetMapping("/wallet/{walletId}/status/{status}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByWalletIdAndStatus(
            @PathVariable Long walletId, 
            @PathVariable Transaction.TransactionStatus status) {
        log.info("Getting transactions for wallet ID: {} and status: {}", walletId, status);
        List<Transaction> transactions = transactionService.getTransactionsByWalletIdAndStatus(walletId, status);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    /**
     * Get all transactions
     */
    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        log.info("Getting all transactions");
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    /**
     * Get transactions by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByStatus(@PathVariable Transaction.TransactionStatus status) {
        log.info("Getting transactions by status: {}", status);
        List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    /**
     * Get pending transactions
     */
    @GetMapping("/pending")
    public ResponseEntity<List<TransactionDto>> getPendingTransactions() {
        log.info("Getting pending transactions");
        List<Transaction> transactions = transactionService.getPendingTransactions();
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    /**
     * Get transactions by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByType(@PathVariable Transaction.TransactionType type) {
        log.info("Getting transactions by type: {}", type);
        List<Transaction> transactions = transactionService.getTransactionsByType(type);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    /**
     * Get transactions by wallet ID and type
     */
    @GetMapping("/wallet/{walletId}/type/{type}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByWalletIdAndType(
            @PathVariable Long walletId, 
            @PathVariable Transaction.TransactionType type) {
        log.info("Getting transactions for wallet ID: {} and type: {}", walletId, type);
        List<Transaction> transactions = transactionService.getTransactionsByWalletIdAndType(walletId, type);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    /**
     * Get deposit transactions for a wallet
     */
    @GetMapping("/wallet/{walletId}/deposits")
    public ResponseEntity<List<TransactionDto>> getDepositTransactionsByWalletId(@PathVariable Long walletId) {
        log.info("Getting deposit transactions for wallet ID: {}", walletId);
        List<Transaction> transactions = transactionService.getDepositTransactionsByWalletId(walletId);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    /**
     * Get withdrawal transactions for a wallet
     */
    @GetMapping("/wallet/{walletId}/withdrawals")
    public ResponseEntity<List<TransactionDto>> getWithdrawTransactionsByWalletId(@PathVariable Long walletId) {
        log.info("Getting withdrawal transactions for wallet ID: {}", walletId);
        List<Transaction> transactions = transactionService.getWithdrawTransactionsByWalletId(walletId);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }
} 