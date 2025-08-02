package com.digitalwallet.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Wallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false)
    private String walletName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;
    
    @Column(nullable = false)
    private boolean activeForShopping = true;
    
    @Column(nullable = false)
    private boolean activeForWithdraw = true;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal usableBalance = BigDecimal.ZERO;
    
    public enum Currency {
        TRY, USD, EUR
    }
} 