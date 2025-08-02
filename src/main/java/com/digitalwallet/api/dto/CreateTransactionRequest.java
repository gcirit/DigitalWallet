package com.digitalwallet.api.dto;

import com.digitalwallet.api.entity.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionRequest {
    
    private Long walletId;
    private BigDecimal amount;
    private Transaction.TransactionType type;
    private Transaction.OppositePartyType oppositePartyType;
    private String oppositeParty;
    
    // Convert to Entity
    public Transaction toEntity() {
        Transaction transaction = new Transaction();
        transaction.setAmount(this.amount);
        transaction.setType(this.type);
        transaction.setOppositePartyType(this.oppositePartyType);
        transaction.setOppositeParty(this.oppositeParty);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        return transaction;
    }
} 