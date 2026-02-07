package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.*;
import top.leonam.hotbctgamess.model.enums.TransactionStatus;
import top.leonam.hotbctgamess.model.enums.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_bank")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false ,unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_account_id")
    private Account originAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    @Column(name = "amount", nullable = false, precision = 30, scale = 10)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "type_transaction", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeTransaction typeTransaction;

    @Column(name = "status_transaction", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
