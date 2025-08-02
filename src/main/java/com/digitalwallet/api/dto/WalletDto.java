package com.digitalwallet.api.dto;

import com.digitalwallet.api.entity.Wallet;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletDto {
    
    private Long id;
    private CustomerDto customer;
    private String walletName;
    private Wallet.Currency currency;
    private boolean activeForShopping;
    private boolean activeForWithdraw;
    private BigDecimal balance;
    private BigDecimal usableBalance;
    
    // Convert from Entity to DTO
    public static WalletDto fromEntity(Wallet wallet) {
        return WalletDto.builder()
                .id(wallet.getId())
                .customer(CustomerDto.fromEntity(wallet.getCustomer()))
                .walletName(wallet.getWalletName())
                .currency(wallet.getCurrency())
                .activeForShopping(wallet.isActiveForShopping())
                .activeForWithdraw(wallet.isActiveForWithdraw())
                .balance(wallet.getBalance())
                .usableBalance(wallet.getUsableBalance())
                .build();
    }
    
    // Convert from DTO to Entity (without customer for creation)
    public Wallet toEntity() {
        Wallet wallet = new Wallet();
        wallet.setId(this.id);
        wallet.setWalletName(this.walletName);
        wallet.setCurrency(this.currency);
        wallet.setActiveForShopping(this.activeForShopping);
        wallet.setActiveForWithdraw(this.activeForWithdraw);
        wallet.setBalance(this.balance);
        wallet.setUsableBalance(this.usableBalance);
        return wallet;
    }
} 