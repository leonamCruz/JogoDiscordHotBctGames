package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "crimes")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Crime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crime_id", nullable = false ,unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "level_min", nullable = false)
    private Integer minLevel;

    @Column(name = "xp", nullable = false)
    private Integer xp;

    @Column(name = "min_reward", nullable = false, precision = 30, scale = 10)
    private BigDecimal minReward;

    @Column(name = "max_reward", nullable = false, precision = 30, scale = 10)
    private BigDecimal maxReward;

    @Column(name = "cooldown_seconds", nullable = false)
    private Integer cooldownSeconds;

    @Column(name = "success_change", nullable = false)
    private Integer successChance;

    @Column(name = "police_risk", nullable = false)
    private Integer policeRisk;

    @PrePersist
    @PreUpdate
    public void setup() {
        if (successChance < 0 || successChance > 100) throw new IllegalArgumentException("successChance deve estar entre 0 e 100");
        if (this.policeRisk < 0 || this.policeRisk > 100) throw new IllegalArgumentException("policeRisk deve estar entre 0 e 100");

        if (minReward.compareTo(maxReward) > 0) throw new IllegalStateException("minReward não pode ser maior que maxReward");
    }
}

