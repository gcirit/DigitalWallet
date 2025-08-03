package com.digitalwallet.api.controller;

import com.digitalwallet.api.dto.TransactionDto;
import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.entity.Transaction;
import com.digitalwallet.api.service.AuthService;
import com.digitalwallet.api.service.TransactionService;
import com.digitalwallet.api.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthService authService;
    private final WalletService walletService;

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
            // Check authorization - only EMPLOYEE, ADMIN, or the wallet owner can create transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can deposit to any wallet (no restriction)
            if (authService.isCustomer()) {
                // Customers can deposit to any wallet - no ownership check needed
                // This allows customers to send money to other customers
            }
            
            Transaction transaction = transactionService.createDepositTransaction(walletId, amount, oppositePartyType, oppositeParty);
            return ResponseEntity.status(HttpStatus.CREATED).body(TransactionDto.fromEntity(transaction));
        } catch (IllegalArgumentException e) {
            log.error("Error creating deposit transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
            // Check authorization - only EMPLOYEE, ADMIN, or the wallet owner can create transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only withdraw from their own wallets
            if (authService.isCustomer()) {
                // Check if the wallet belongs to the current customer
                if (!walletService.isWalletOwnedByCustomer(walletId, currentCustomer.getId())) {
                    throw new AccessDeniedException("Customers can only withdraw from their own wallets");
                }
            }
            
            Transaction transaction = transactionService.createWithdrawTransaction(walletId, amount, oppositePartyType, oppositeParty);
            return ResponseEntity.status(HttpStatus.CREATED).body(TransactionDto.fromEntity(transaction));
        } catch (IllegalArgumentException e) {
            log.error("Error creating withdrawal transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Approve a transaction
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<TransactionDto> approveTransaction(@PathVariable Long id) {
        log.info("Approving transaction with ID: {}", id);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can approve transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can approve transactions
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can approve transactions");
            }
            
            Transaction approvedTransaction = transactionService.approveTransaction(id);
            return ResponseEntity.ok(TransactionDto.fromEntity(approvedTransaction));
        } catch (IllegalArgumentException e) {
            log.error("Error approving transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Deny a transaction
     */
    @PutMapping("/{id}/deny")
    public ResponseEntity<TransactionDto> denyTransaction(@PathVariable Long id) {
        log.info("Denying transaction with ID: {}", id);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can deny transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can deny transactions
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can deny transactions");
            }
            
            Transaction deniedTransaction = transactionService.denyTransaction(id);
            return ResponseEntity.ok(TransactionDto.fromEntity(deniedTransaction));
        } catch (IllegalArgumentException e) {
            log.error("Error denying transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get transaction by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        log.info("Getting transaction by ID: {}", id);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the transaction owner can view transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only view their own transactions
            if (authService.isCustomer()) {
                // Check if the transaction belongs to the current customer
                Optional<Transaction> transaction = transactionService.getTransactionById(id);
                if (transaction.isEmpty() || !transaction.get().getWallet().getCustomer().getId().equals(currentCustomer.getId())) {
                    throw new AccessDeniedException("Customers can only view their own transactions");
                }
            }
            
            return transactionService.getTransactionById(id)
                    .map(TransactionDto::fromEntity)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get all transactions for a wallet
     */
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByWalletId(@PathVariable Long walletId) {
        log.info("Getting transactions for wallet ID: {}", walletId);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the wallet owner can view transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only view transactions for their own wallets
            if (authService.isCustomer()) {
                // Check if the wallet belongs to the current customer
                if (!walletService.isWalletOwnedByCustomer(walletId, currentCustomer.getId())) {
                    throw new AccessDeniedException("Customers can only view transactions for their own wallets");
                }
            }
            
            List<Transaction> transactions = transactionService.getTransactionsByWalletId(walletId);
            List<TransactionDto> transactionDtos = transactions.stream()
                    .map(TransactionDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get transactions by wallet ID and status
     */
    @GetMapping("/wallet/{walletId}/status/{status}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByWalletIdAndStatus(
            @PathVariable Long walletId, 
            @PathVariable Transaction.TransactionStatus status) {
        log.info("Getting transactions for wallet ID: {} and status: {}", walletId, status);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the wallet owner can view transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only view transactions for their own wallets
            if (authService.isCustomer()) {
                // Check if the wallet belongs to the current customer
                if (!walletService.isWalletOwnedByCustomer(walletId, currentCustomer.getId())) {
                    throw new AccessDeniedException("Customers can only view transactions for their own wallets");
                }
            }
            
            List<Transaction> transactions = transactionService.getTransactionsByWalletIdAndStatus(walletId, status);
            List<TransactionDto> transactionDtos = transactions.stream()
                    .map(TransactionDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get all transactions (EMPLOYEE/ADMIN see all, CUSTOMER sees their own)
     */
    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        log.info("Getting all transactions");
        try {
            // Check authorization
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            List<Transaction> transactions;
            List<TransactionDto> transactionDtos;
            
            // If current user is EMPLOYEE or ADMIN, they can view all transactions
            if (authService.isEmployeeOrAdmin()) {
                transactions = transactionService.getAllTransactions();
            } else {
                // If current user is CUSTOMER, they can only view their own transactions
                transactions = transactionService.getTransactionsByCustomerId(currentCustomer.getId());
            }
            
            transactionDtos = transactions.stream()
                    .map(TransactionDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get transactions by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByStatus(@PathVariable Transaction.TransactionStatus status) {
        log.info("Getting transactions by status: {}", status);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can view transactions by status
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can view transactions by status
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can view transactions by status");
            }
            
            List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
            List<TransactionDto> transactionDtos = transactions.stream()
                    .map(TransactionDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get pending transactions
     */
    @GetMapping("/pending")
    public ResponseEntity<List<TransactionDto>> getPendingTransactions() {
        log.info("Getting pending transactions");
        try {
            // Check authorization - only EMPLOYEE or ADMIN can view pending transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can view pending transactions
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can view pending transactions");
            }
            
            List<Transaction> transactions = transactionService.getPendingTransactions();
            List<TransactionDto> transactionDtos = transactions.stream()
                    .map(TransactionDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get transactions by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByType(@PathVariable Transaction.TransactionType type) {
        log.info("Getting transactions by type: {}", type);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can view transactions by type
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can view transactions by type
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can view transactions by type");
            }
            
            List<Transaction> transactions = transactionService.getTransactionsByType(type);
            List<TransactionDto> transactionDtos = transactions.stream()
                    .map(TransactionDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get transactions by wallet ID and type
     */
    @GetMapping("/wallet/{walletId}/type/{type}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByWalletIdAndType(
            @PathVariable Long walletId, 
            @PathVariable Transaction.TransactionType type) {
        log.info("Getting transactions for wallet ID: {} and type: {}", walletId, type);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the wallet owner can view transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only view transactions for their own wallets
            if (authService.isCustomer()) {
                // Check if the wallet belongs to the current customer
                if (!walletService.isWalletOwnedByCustomer(walletId, currentCustomer.getId())) {
                    throw new AccessDeniedException("Customers can only view transactions for their own wallets");
                }
            }
            
            List<Transaction> transactions = transactionService.getTransactionsByWalletIdAndType(walletId, type);
            List<TransactionDto> transactionDtos = transactions.stream()
                    .map(TransactionDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get deposit transactions for a wallet
     */
    @GetMapping("/wallet/{walletId}/deposits")
    public ResponseEntity<List<TransactionDto>> getDepositTransactionsByWalletId(@PathVariable Long walletId) {
        log.info("Getting deposit transactions for wallet ID: {}", walletId);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the wallet owner can view transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only view transactions for their own wallets
            if (authService.isCustomer()) {
                // Check if the wallet belongs to the current customer
                if (!walletService.isWalletOwnedByCustomer(walletId, currentCustomer.getId())) {
                    throw new AccessDeniedException("Customers can only view transactions for their own wallets");
                }
            }
            
            List<Transaction> transactions = transactionService.getDepositTransactionsByWalletId(walletId);
            List<TransactionDto> transactionDtos = transactions.stream()
                    .map(TransactionDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get withdrawal transactions for a wallet
     */
    @GetMapping("/wallet/{walletId}/withdrawals")
    public ResponseEntity<List<TransactionDto>> getWithdrawTransactionsByWalletId(@PathVariable Long walletId) {
        log.info("Getting withdrawal transactions for wallet ID: {}", walletId);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the wallet owner can view transactions
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only view transactions for their own wallets
            if (authService.isCustomer()) {
                // Check if the wallet belongs to the current customer
                if (!walletService.isWalletOwnedByCustomer(walletId, currentCustomer.getId())) {
                    throw new AccessDeniedException("Customers can only view transactions for their own wallets");
                }
            }
            
            List<Transaction> transactions = transactionService.getWithdrawTransactionsByWalletId(walletId);
            List<TransactionDto> transactionDtos = transactions.stream()
                    .map(TransactionDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
} 