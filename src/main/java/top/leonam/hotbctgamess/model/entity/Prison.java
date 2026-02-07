package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.*;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "prison")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Prison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prison_id", nullable = false ,unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false, unique = true)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PrisonStatus status;

    @Column(name = "jailed_at")
    private LocalDateTime jailedAt;

    @Column(name = "release_at")
    private LocalDateTime releaseAt;

    @PrePersist
    public void prePersist() {
        status = PrisonStatus.SOLTO;
    }

}
