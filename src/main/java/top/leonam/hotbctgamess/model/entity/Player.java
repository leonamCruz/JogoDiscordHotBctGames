package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Entity
@Data
@NoArgsConstructor
public class Player {

    public Player(Identity identity) {
        this.identity = identity;

        this.economy = new Economy();
        this.economy.setPlayer(this);

        this.job = new Job();
        this.job.setPlayer(this);

        this.crime = new Crime();
        this.crime.setPlayer(this);

        this.level = new Level();
        this.level.setPlayer(this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "identity_id", nullable = false)
    private Identity identity;

    @OneToMany(mappedBy = "player")
    private Set<Product> products;

    @OneToMany(mappedBy = "player")
    private Set<Mining> minings;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "economy_id", nullable = false)
    private Economy economy;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "crime_id", nullable = false)
    private Crime crime;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;
}
