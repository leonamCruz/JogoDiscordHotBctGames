package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "prison_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PrisonHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prison_history_id", nullable = false ,unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "date_of_arrest", nullable = false, updatable = false)
    private LocalDateTime dateOfArrest;

    @Column(name = "release_forecast", nullable = false, updatable = false)
    private LocalDateTime releaseForecast;

    @PrePersist
    public void prePersist() {
        dateOfArrest = LocalDateTime.now();
        releaseForecast = LocalDateTime.now();
    }

}
