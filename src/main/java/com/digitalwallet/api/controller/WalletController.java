package com.digitalwallet.api.controller;

import com.digitalwallet.api.dto.WalletDto;
import com.digitalwallet.api.dto.CreateWalletRequest;
import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Employee;
import com.digitalwallet.api.entity.Wallet;
import com.digitalwallet.api.service.AuthService;
import com.digitalwallet.api.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;
    private final AuthService authService;

    /**
     * Create a new wallet for a customer
     */
    @PostMapping("/customer/{customerId}")
    public ResponseEntity<WalletDto> createWallet(@PathVariable Long customerId, @RequestBody CreateWalletRequest request) {
        log.info("Creating wallet for customer ID: {}", customerId);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the customer themselves can create wallets
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only create wallets for themselves
            if (authService.isCustomer() && !currentCustomer.getId().equals(customerId)) {
                throw new AccessDeniedException("Customers can only create wallets for themselves");
            }
            
            // If current user is EMPLOYEE or ADMIN, they can create wallets for any customer
            if (authService.isEmployeeOrAdmin()) {
                // Allow employee/admin to create wallet for any customer
            }
            
            Wallet wallet = request.toEntity();
            Wallet createdWallet = walletService.createWallet(customerId, wallet);
            return ResponseEntity.status(HttpStatus.CREATED).body(WalletDto.fromEntity(createdWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error creating wallet: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Create a new wallet for the current authenticated customer
     */
    @PostMapping("/me")
    public ResponseEntity<WalletDto> createWalletForCurrentCustomer(@RequestBody CreateWalletRequest request) {
        log.info("Creating wallet for current customer");
        try {
            // Get current customer from authentication context
            // For now, we'll use a default customer ID (1) - in production, you'd get this from the security context
            Long currentCustomerId = 1L; // This should be extracted from authentication
            Wallet wallet = request.toEntity();
            Wallet createdWallet = walletService.createWallet(currentCustomerId, wallet);
            return ResponseEntity.status(HttpStatus.CREATED).body(WalletDto.fromEntity(createdWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error creating wallet: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get wallet by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WalletDto> getWalletById(@PathVariable Long id) {
        log.info("Getting wallet by ID: {}", id);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the wallet owner can view wallet
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only view their own wallets
            if (authService.isCustomer()) {
                // TODO: Add wallet ownership check - for now, allow customer to view wallets
                // In production, you'd check if the wallet belongs to the current customer
            }
            
            return walletService.getWalletById(id)
                    .map(WalletDto::fromEntity)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get all wallets for a customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<WalletDto>> getWalletsByCustomerId(@PathVariable Long customerId) {
        log.info("Getting wallets for customer ID: {}", customerId);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the customer themselves can view wallets
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only view their own wallets
            if (authService.isCustomer() && !currentCustomer.getId().equals(customerId)) {
                throw new AccessDeniedException("Customers can only view their own wallets");
            }
            
            List<Wallet> wallets = walletService.getWalletsByCustomerId(customerId);
            List<WalletDto> walletDtos = wallets.stream()
                    .map(WalletDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(walletDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get wallets by customer ID and currency
     */
    @GetMapping("/customer/{customerId}/currency/{currency}")
    public ResponseEntity<List<WalletDto>> getWalletsByCustomerIdAndCurrency(
            @PathVariable Long customerId, 
            @PathVariable Wallet.Currency currency) {
        log.info("Getting wallets for customer ID: {} and currency: {}", customerId, currency);
        try {
            // Check authorization - only EMPLOYEE, ADMIN, or the customer themselves can view wallets
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // If current user is CUSTOMER, they can only view their own wallets
            if (authService.isCustomer() && !currentCustomer.getId().equals(customerId)) {
                throw new AccessDeniedException("Customers can only view their own wallets");
            }
            
            List<Wallet> wallets = walletService.getWalletsByCustomerIdAndCurrency(customerId, currency);
            List<WalletDto> walletDtos = wallets.stream()
                    .map(WalletDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(walletDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get all wallets (EMPLOYEE only)
     */
    @GetMapping
    public ResponseEntity<List<WalletDto>> getAllWallets() {
        log.info("Getting all wallets");
        try {
            // Check authorization - only EMPLOYEE or ADMIN can view all wallets
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can view all wallets
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can view all wallets");
            }
            
            List<Wallet> wallets = walletService.getAllWallets();
            List<WalletDto> walletDtos = wallets.stream()
                .map(WalletDto::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(walletDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Update wallet balance (EMPLOYEE only)
     */
    @PutMapping("/{id}/balance")
    public ResponseEntity<WalletDto> updateWalletBalance(
            @PathVariable Long id, 
            @RequestBody BigDecimal newBalance) {
        log.info("Updating wallet balance for wallet ID: {}", id);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can directly update wallet balance
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can directly update wallet balance
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can directly update wallet balance");
            }
            
            Wallet updatedWallet = walletService.updateWalletBalance(id, newBalance);
            return ResponseEntity.ok(WalletDto.fromEntity(updatedWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error updating wallet balance: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Add amount to wallet balance (EMPLOYEE only)
     */
    @PostMapping("/{id}/balance/add")
    public ResponseEntity<WalletDto> addToWalletBalance(
            @PathVariable Long id, 
            @RequestBody BigDecimal amount) {
        log.info("Adding {} to wallet balance for wallet ID: {}", amount, id);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can directly add balance
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can directly add balance to wallets
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can directly add balance to wallets");
            }
            
            Wallet updatedWallet = walletService.addToWalletBalance(id, amount);
            return ResponseEntity.ok(WalletDto.fromEntity(updatedWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error adding to wallet balance: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Deduct amount from wallet balance (EMPLOYEE only)
     */
    @PostMapping("/{id}/balance/deduct")
    public ResponseEntity<WalletDto> deductFromWalletBalance(
            @PathVariable Long id, 
            @RequestBody BigDecimal amount) {
        log.info("Deducting {} from wallet balance for wallet ID: {}", amount, id);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can directly deduct balance
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can directly deduct balance from wallets
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can directly deduct balance from wallets");
            }
            
            Wallet updatedWallet = walletService.deductFromWalletBalance(id, amount);
            return ResponseEntity.ok(WalletDto.fromEntity(updatedWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error deducting from wallet balance: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Update wallet status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<WalletDto> updateWalletStatus(
            @PathVariable Long id,
            @RequestParam boolean activeForShopping,
            @RequestParam boolean activeForWithdraw) {
        log.info("Updating wallet status for wallet ID: {}", id);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can update wallet status
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can update wallet status
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can update wallet status");
            }
            
            Wallet updatedWallet = walletService.updateWalletStatus(id, activeForShopping, activeForWithdraw);
            return ResponseEntity.ok(WalletDto.fromEntity(updatedWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error updating wallet status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Delete wallet
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        log.info("Deleting wallet with ID: {}", id);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can delete wallets
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can delete wallets
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can delete wallets");
            }
            
            walletService.deleteWallet(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting wallet: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get wallets by currency
     */
    @GetMapping("/currency/{currency}")
    public ResponseEntity<List<WalletDto>> getWalletsByCurrency(@PathVariable Wallet.Currency currency) {
        log.info("Getting wallets by currency: {}", currency);
        try {
            // Check authorization - only EMPLOYEE or ADMIN can view wallets by currency
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can view wallets by currency
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can view wallets by currency");
            }
            
            List<Wallet> wallets = walletService.getWalletsByCurrency(currency);
            List<WalletDto> walletDtos = wallets.stream()
                    .map(WalletDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(walletDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get active wallets for shopping
     */
    @GetMapping("/active/shopping")
    public ResponseEntity<List<WalletDto>> getActiveWalletsForShopping() {
        log.info("Getting active wallets for shopping");
        try {
            // Check authorization - only EMPLOYEE or ADMIN can view active wallets for shopping
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can view active wallets for shopping
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can view active wallets for shopping");
            }
            
            List<Wallet> wallets = walletService.getActiveWalletsForShopping();
            List<WalletDto> walletDtos = wallets.stream()
                    .map(WalletDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(walletDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get active wallets for withdrawal
     */
    @GetMapping("/active/withdraw")
    public ResponseEntity<List<WalletDto>> getActiveWalletsForWithdraw() {
        log.info("Getting active wallets for withdrawal");
        try {
            // Check authorization - only EMPLOYEE or ADMIN can view active wallets for withdrawal
            Customer currentCustomer = authService.getCurrentCustomer();
            Employee currentEmployee = authService.getCurrentEmployee();
            
            if (currentCustomer == null && currentEmployee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Only EMPLOYEE or ADMIN can view active wallets for withdrawal
            if (!authService.isEmployeeOrAdmin()) {
                throw new AccessDeniedException("Only employees or admins can view active wallets for withdrawal");
            }
            
            List<Wallet> wallets = walletService.getActiveWalletsForWithdraw();
            List<WalletDto> walletDtos = wallets.stream()
                    .map(WalletDto::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(walletDtos);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
} 