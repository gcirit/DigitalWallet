package com.digitalwallet.api.controller;

import com.digitalwallet.api.dto.WalletDto;
import com.digitalwallet.api.dto.CreateWalletRequest;
import com.digitalwallet.api.entity.Wallet;
import com.digitalwallet.api.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * Create a new wallet for a customer
     */
    @PostMapping("/customer/{customerId}")
    public ResponseEntity<WalletDto> createWallet(@PathVariable Long customerId, @RequestBody CreateWalletRequest request) {
        log.info("Creating wallet for customer ID: {}", customerId);
        try {
            Wallet wallet = request.toEntity();
            Wallet createdWallet = walletService.createWallet(customerId, wallet);
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
        return walletService.getWalletById(id)
                .map(WalletDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all wallets for a customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<WalletDto>> getWalletsByCustomerId(@PathVariable Long customerId) {
        log.info("Getting wallets for customer ID: {}", customerId);
        List<Wallet> wallets = walletService.getWalletsByCustomerId(customerId);
        List<WalletDto> walletDtos = wallets.stream()
                .map(WalletDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(walletDtos);
    }

    /**
     * Get wallets by customer ID and currency
     */
    @GetMapping("/customer/{customerId}/currency/{currency}")
    public ResponseEntity<List<WalletDto>> getWalletsByCustomerIdAndCurrency(
            @PathVariable Long customerId, 
            @PathVariable Wallet.Currency currency) {
        log.info("Getting wallets for customer ID: {} and currency: {}", customerId, currency);
        List<Wallet> wallets = walletService.getWalletsByCustomerIdAndCurrency(customerId, currency);
        List<WalletDto> walletDtos = wallets.stream()
                .map(WalletDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(walletDtos);
    }

    /**
     * Get all wallets
     */
    @GetMapping
    public ResponseEntity<List<WalletDto>> getAllWallets() {
        log.info("Getting all wallets");
        List<Wallet> wallets = walletService.getAllWallets();
        List<WalletDto> walletDtos = wallets.stream()
                .map(WalletDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(walletDtos);
    }

    /**
     * Update wallet balance
     */
    @PutMapping("/{id}/balance")
    public ResponseEntity<WalletDto> updateWalletBalance(
            @PathVariable Long id, 
            @RequestBody BigDecimal newBalance) {
        log.info("Updating wallet balance for wallet ID: {}", id);
        try {
            Wallet updatedWallet = walletService.updateWalletBalance(id, newBalance);
            return ResponseEntity.ok(WalletDto.fromEntity(updatedWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error updating wallet balance: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Add amount to wallet balance
     */
    @PostMapping("/{id}/balance/add")
    public ResponseEntity<WalletDto> addToWalletBalance(
            @PathVariable Long id, 
            @RequestBody BigDecimal amount) {
        log.info("Adding {} to wallet balance for wallet ID: {}", amount, id);
        try {
            Wallet updatedWallet = walletService.addToWalletBalance(id, amount);
            return ResponseEntity.ok(WalletDto.fromEntity(updatedWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error adding to wallet balance: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deduct amount from wallet balance
     */
    @PostMapping("/{id}/balance/deduct")
    public ResponseEntity<WalletDto> deductFromWalletBalance(
            @PathVariable Long id, 
            @RequestBody BigDecimal amount) {
        log.info("Deducting {} from wallet balance for wallet ID: {}", amount, id);
        try {
            Wallet updatedWallet = walletService.deductFromWalletBalance(id, amount);
            return ResponseEntity.ok(WalletDto.fromEntity(updatedWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error deducting from wallet balance: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
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
            Wallet updatedWallet = walletService.updateWalletStatus(id, activeForShopping, activeForWithdraw);
            return ResponseEntity.ok(WalletDto.fromEntity(updatedWallet));
        } catch (IllegalArgumentException e) {
            log.error("Error updating wallet status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete wallet
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        log.info("Deleting wallet with ID: {}", id);
        try {
            walletService.deleteWallet(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting wallet: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get wallets by currency
     */
    @GetMapping("/currency/{currency}")
    public ResponseEntity<List<WalletDto>> getWalletsByCurrency(@PathVariable Wallet.Currency currency) {
        log.info("Getting wallets by currency: {}", currency);
        List<Wallet> wallets = walletService.getWalletsByCurrency(currency);
        List<WalletDto> walletDtos = wallets.stream()
                .map(WalletDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(walletDtos);
    }

    /**
     * Get active wallets for shopping
     */
    @GetMapping("/active/shopping")
    public ResponseEntity<List<WalletDto>> getActiveWalletsForShopping() {
        log.info("Getting active wallets for shopping");
        List<Wallet> wallets = walletService.getActiveWalletsForShopping();
        List<WalletDto> walletDtos = wallets.stream()
                .map(WalletDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(walletDtos);
    }

    /**
     * Get active wallets for withdrawal
     */
    @GetMapping("/active/withdraw")
    public ResponseEntity<List<WalletDto>> getActiveWalletsForWithdraw() {
        log.info("Getting active wallets for withdrawal");
        List<Wallet> wallets = walletService.getActiveWalletsForWithdraw();
        List<WalletDto> walletDtos = wallets.stream()
                .map(WalletDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(walletDtos);
    }
} 