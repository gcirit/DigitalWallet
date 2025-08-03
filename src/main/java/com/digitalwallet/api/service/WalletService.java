package com.digitalwallet.api.service;

import com.digitalwallet.api.entity.Customer;
import com.digitalwallet.api.entity.Wallet;
import com.digitalwallet.api.repository.CustomerRepository;
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
public class WalletService {

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;

    /**
     * Create a new wallet for a customer
     */
    public Wallet createWallet(Long customerId, Wallet wallet) {
        log.info("Creating wallet for customer ID: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
        
        wallet.setCustomer(customer);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUsableBalance(BigDecimal.ZERO);
        
        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet created successfully with ID: {}", savedWallet.getId());
        return savedWallet;
    }

    /**
     * Get wallet by ID
     */
    @Transactional(readOnly = true)
    public Optional<Wallet> getWalletById(Long id) {
        return walletRepository.findById(id);
    }

    /**
     * Get all wallets for a customer
     */
    @Transactional(readOnly = true)
    public List<Wallet> getWalletsByCustomerId(Long customerId) {
        return walletRepository.findByCustomerId(customerId);
    }

    /**
     * Get wallets by customer ID and currency
     */
    @Transactional(readOnly = true)
    public List<Wallet> getWalletsByCustomerIdAndCurrency(Long customerId, Wallet.Currency currency) {
        return walletRepository.findByCustomerIdAndCurrency(customerId, currency);
    }

    /**
     * Get all wallets
     */
    @Transactional(readOnly = true)
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    /**
     * Update wallet balance
     */
    public Wallet updateWalletBalance(Long walletId, BigDecimal newBalance) {
        log.info("Updating wallet balance for wallet ID: {}", walletId);
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));
        
        wallet.setBalance(newBalance);
        wallet.setUsableBalance(newBalance); // For simplicity, usable balance equals balance
        
        Wallet updatedWallet = walletRepository.save(wallet);
        log.info("Wallet balance updated successfully");
        return updatedWallet;
    }

    /**
     * Add amount to wallet balance
     */
    public Wallet addToWalletBalance(Long walletId, BigDecimal amount) {
        log.info("Adding {} to wallet balance for wallet ID: {}", amount, walletId);
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));
        
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        wallet.setUsableBalance(newBalance); // For simplicity, usable balance equals balance
        
        Wallet updatedWallet = walletRepository.save(wallet);
        log.info("Amount added to wallet balance successfully");
        return updatedWallet;
    }

    /**
     * Deduct amount from wallet balance
     */
    public Wallet deductFromWalletBalance(Long walletId, BigDecimal amount) {
        log.info("Deducting {} from wallet balance for wallet ID: {}", amount, walletId);
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Available: " + wallet.getBalance() + ", Required: " + amount);
        }
        
        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        wallet.setUsableBalance(newBalance); // For simplicity, usable balance equals balance
        
        Wallet updatedWallet = walletRepository.save(wallet);
        log.info("Amount deducted from wallet balance successfully");
        return updatedWallet;
    }

    /**
     * Update wallet status (active for shopping/withdraw)
     */
    public Wallet updateWalletStatus(Long walletId, boolean activeForShopping, boolean activeForWithdraw) {
        log.info("Updating wallet status for wallet ID: {}", walletId);
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));
        
        wallet.setActiveForShopping(activeForShopping);
        wallet.setActiveForWithdraw(activeForWithdraw);
        
        Wallet updatedWallet = walletRepository.save(wallet);
        log.info("Wallet status updated successfully");
        return updatedWallet;
    }

    /**
     * Delete wallet
     */
    public void deleteWallet(Long id) {
        log.info("Deleting wallet with ID: {}", id);
        
        if (!walletRepository.existsById(id)) {
            throw new IllegalArgumentException("Wallet not found with ID: " + id);
        }
        
        walletRepository.deleteById(id);
        log.info("Wallet deleted successfully");
    }

    /**
     * Check if wallet exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return walletRepository.existsById(id);
    }

    /**
     * Get wallets by currency
     */
    @Transactional(readOnly = true)
    public List<Wallet> getWalletsByCurrency(Wallet.Currency currency) {
        return walletRepository.findByCurrency(currency);
    }

    /**
     * Get active wallets for shopping
     */
    @Transactional(readOnly = true)
    public List<Wallet> getActiveWalletsForShopping() {
        return walletRepository.findByActiveForShoppingTrue();
    }

    /**
     * Get active wallets for withdrawal
     */
    @Transactional(readOnly = true)
    public List<Wallet> getActiveWalletsForWithdraw() {
        return walletRepository.findByActiveForWithdrawTrue();
    }

    /**
     * Check if a wallet is owned by a specific customer
     */
    @Transactional(readOnly = true)
    public boolean isWalletOwnedByCustomer(Long walletId, Long customerId) {
        Optional<Wallet> wallet = walletRepository.findById(walletId);
        return wallet.isPresent() && wallet.get().getCustomer().getId().equals(customerId);
    }
} 