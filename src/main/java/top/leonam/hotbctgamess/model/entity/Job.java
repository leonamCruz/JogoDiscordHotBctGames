package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime lastJob;

    @Column(nullable = false)
    private Long totalDeliveries;

    @OneToOne(mappedBy = "job")
    private Player player;

    @PrePersist
    public void setup() {
        totalDeliveries = 0L;
    }
}
