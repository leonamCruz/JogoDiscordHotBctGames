package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Prison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(mappedBy = "prison")
    private Player player;
    private Boolean active;
    private LocalDateTime lastPrison;

    @PrePersist
    public void prePersist() {
        active = false;
    }
}
