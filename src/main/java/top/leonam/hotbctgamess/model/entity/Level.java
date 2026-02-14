package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Long level;
    private Long xp;
    @OneToOne(mappedBy = "level")
    private Player player;

    @PrePersist
    public void setup(){
        this.level = 0L;
        this.xp = 0L;
    }

    public long xpParaProximoNivel() {
        return 100 * level * level;
    }

    public void adicionarXp(Long valor) {
        if (valor <= 0) return;

        xp += valor;
        while (xp >= xpParaProximoNivel()) {
            xp -= xpParaProximoNivel();
            level++;
        }
    }
}
