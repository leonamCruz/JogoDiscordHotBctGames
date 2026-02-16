package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Entity
@Table(indexes = {
        @Index(name = "idx_player_identity_id", columnList = "identity_id"),
        @Index(name = "idx_player_economy_id", columnList = "economy_id"),
        @Index(name = "idx_player_job_id", columnList = "job_id"),
        @Index(name = "idx_player_crime_id", columnList = "crime_id"),
        @Index(name = "idx_player_level_id", columnList = "level_id"),
        @Index(name = "idx_player_prison_id", columnList = "prison_id"),
        @Index(name = "idx_player_university_id", columnList = "university_id")
})
@Data
@NoArgsConstructor
public class Player {

    public Player(Identity identity) {
        this.identity = identity;

        this.economy = new Economy();
        this.economy.setPlayer(this);

        this.job = new Job();
        this.job.setPlayer(this);

        this.level = new Level();
        this.level.setPlayer(this);

        this.prison = new Prison();
        this.prison.setPlayer(this);

        this.university = new University();
        this.university.setPlayer(this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "identity_id", nullable = false)
    private Identity identity;

    @OneToMany(mappedBy = "player")
    private Set<Product> products;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "economy_id", nullable = false)
    private Economy economy;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "prison_id", nullable = false)
    private Prison prison;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;
}
