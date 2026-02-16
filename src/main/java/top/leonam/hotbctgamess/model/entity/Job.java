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

    @OneToOne(mappedBy = "job")
    private Player player;

    @PrePersist
    public void setup() {
        totalJobs = 0L;
        totalCrimes = 0L;
        robberiesToday = 0L;
    }
}
