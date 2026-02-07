package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.*;
import top.leonam.hotbctgamess.model.enums.BankStatus;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "account_bank")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_bank_id", nullable = false ,unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false, unique = true)
    private Player player;

    @Column(name = "balance", nullable = false, precision = 30, scale = 10)
    private BigDecimal balance;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BankStatus status;

    @OneToMany(mappedBy = "originAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> sendTransactions;

    @OneToMany(mappedBy = "destinationAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> receivedTransactions;

    @PrePersist
    public void prePersist() {
        if (this.balance == null) this.balance = BigDecimal.ZERO;
        if (this.status == null) this.status = BankStatus.ATIVO;
    }


}
