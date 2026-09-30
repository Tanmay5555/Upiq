package com.upiq.transaction.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_tx_user_date", columnList = "user_id, date"),
        @Index(name = "idx_tx_user_type_date", columnList = "user_id, type, date"),
        @Index(name = "idx_tx_user_cat_date", columnList = "user_id, category, date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;             // user making the transaction

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String type;             // income/expense or credit/debit

    @Column(nullable = false)
    private String category;         // Food, Travel, Bills, etc.

    private String description;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    private String paymentMethod;    // UPI, Cash, Card
}

