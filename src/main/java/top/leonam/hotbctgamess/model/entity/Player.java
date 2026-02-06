package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity(name = "player")
@Table(name = "player")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id", nullable = false ,unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "identity_id", nullable = false, unique = true)
    private Identity identity;

    @Column(name = "current_level", nullable = false)
    private Integer currentLevel;

    @Column(name = "current_xp", nullable = false)
    private Long currentXp;

    @Column(name = "respect_points", nullable = false)
    private Integer respectPoints;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL)
    private Account account;

    @ManyToMany
    @JoinTable(
            name = "player_achievements",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    private Set<Achievement> achievements;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CrimeHistory> crimeHistories;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Inventory>  inventorys;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Prison prison;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PrisonHistory> prisonHistories;

    @PrePersist
    public void prePersist() {
        currentLevel = 0;
        currentXp = 0L;
        respectPoints = 0;
    }

}
