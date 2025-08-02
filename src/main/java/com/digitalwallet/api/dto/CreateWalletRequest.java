package com.digitalwallet.api.dto;

import com.digitalwallet.api.entity.Wallet;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWalletRequest {
    
    private String walletName;
    private Wallet.Currency currency;
    private boolean activeForShopping;
    private boolean activeForWithdraw;
    
    // Convert to Entity
    public Wallet toEntity() {
        Wallet wallet = new Wallet();
        wallet.setWalletName(this.walletName);
        wallet.setCurrency(this.currency);
        wallet.setActiveForShopping(this.activeForShopping);
        wallet.setActiveForWithdraw(this.activeForWithdraw);
        return wallet;
    }
} 