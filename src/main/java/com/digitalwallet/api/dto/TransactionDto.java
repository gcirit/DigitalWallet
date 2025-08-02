package com.digitalwallet.api.dto;

import com.digitalwallet.api.entity.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    
    private Long id;
    private WalletDto wallet;
    private BigDecimal amount;
    private Transaction.TransactionType type;
    private Transaction.OppositePartyType oppositePartyType;
    private String oppositeParty;
    private Transaction.TransactionStatus status;
    private LocalDateTime createdAt;
    
    // Convert from Entity to DTO
    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .wallet(WalletDto.fromEntity(transaction.getWallet()))
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .oppositePartyType(transaction.getOppositePartyType())
                .oppositeParty(transaction.getOppositeParty())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
    
    // Convert from DTO to Entity (without wallet for creation)
    public Transaction toEntity() {
        Transaction transaction = new Transaction();
        transaction.setId(this.id);
        transaction.setAmount(this.amount);
        transaction.setType(this.type);
        transaction.setOppositePartyType(this.oppositePartyType);
        transaction.setOppositeParty(this.oppositeParty);
        transaction.setStatus(this.status);
        transaction.setCreatedAt(this.createdAt);
        return transaction;
    }
} 