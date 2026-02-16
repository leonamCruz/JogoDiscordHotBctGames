package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime lastJob;
    private LocalDateTime lastCrime;
    private LocalDate lastRobberyDate;

    @Column(nullable = false)
    private Long totalJobs;

    @Column(nullable = false)
    private Long totalCrimes;

    @Column(nullable = false)
    private Long robberiesToday;

    @Column(nullable = false)
    private Long totalIfood;

    @Column(nullable = false)
    private Long totalUber;

    @Column(nullable = false)
    private Long totalEstoque;

    @Column(nullable = false)
    private Long totalGarcom;

    @Column(nullable = false)
    private Long totalPedreiro;

    @Column(nullable = false)
    private Long totalCc;

    @Column(nullable = false)
    private Long totalTrafico;

    @Column(nullable = false)
    private Long totalSequestro;

    @Column(nullable = false)
    private Long totalHackear;

    @Column(nullable = false)
    private Long totalLaranja;

    @Column(nullable = false)
    private Long totalBet;

    @Column(nullable = false)
    private Long totalRoubar;

    @OneToOne(mappedBy = "job")
    private Player player;

    @PrePersist
    public void setup() {
        totalJobs = 0L;
        totalCrimes = 0L;
        robberiesToday = 0L;
        totalIfood = 0L;
        totalUber = 0L;
        totalEstoque = 0L;
        totalGarcom = 0L;
        totalPedreiro = 0L;
        totalCc = 0L;
        totalTrafico = 0L;
        totalSequestro = 0L;
        totalHackear = 0L;
        totalLaranja = 0L;
        totalBet = 0L;
        totalRoubar = 0L;
    }
}
