package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crime_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CrimeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crime_history_id", nullable = false ,unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crime_id", nullable = false)
    private Crime crime;

    @Column(name = "attempted_at", nullable = false, updatable = false)
    private LocalDateTime attemptedAt;

    @Column(name = "success", nullable = false)
    private Boolean success;

    @Column(name = "reward", precision = 30, scale = 10)
    private BigDecimal reward;

    @Column(name = "jailed", nullable = false)
    private Boolean jailed;

    @Column(name = "jail_time_seconds")
    private Integer jailTimeSeconds;

    @PrePersist
    private void prePersist() {
        this.attemptedAt = LocalDateTime.now();
    }
}
