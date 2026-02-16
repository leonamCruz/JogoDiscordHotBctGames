package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean conseguiu;

    @Column
    private LocalDateTime ultimaTentativa;

    @Column
    private LocalDateTime quandoConsegui;

    @Column
    private Boolean ultimoResultadoSucesso;

    @Column
    private LocalDateTime ultimoResultadoEm;

    @OneToOne(mappedBy = "university")
    private Player player;

    @PrePersist
    public void prePersist() {
        conseguiu = false;
    }

}
