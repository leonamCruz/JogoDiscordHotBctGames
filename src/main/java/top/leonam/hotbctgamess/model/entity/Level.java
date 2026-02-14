package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long level;
    private long xp;

    @OneToOne(mappedBy = "level")
    private Player player;

    @PrePersist
    public void setup() {
        this.level = 1;
        this.xp = 0;
    }

    public long xpParaProximoNivel() {
        return 100L * level * level;
    }

    public void ganharXp(long valor) {
        if (valor <= 0) return;

        xp += valor;
        subirNivelSeNecessario();
    }

    public void perderXp(long valor) {
        if (valor <= 0) return;

        xp -= valor;
        descerNivelSeNecessario();
    }

    private void subirNivelSeNecessario() {
        while (xp >= xpParaProximoNivel()) {
            xp -= xpParaProximoNivel();
            level++;
        }
    }

    private void descerNivelSeNecessario() {
        while (xp < 0 && level > 1) {
            level--;
            xp += xpParaProximoNivel();
        }

        if (xp < 0) {
            xp = 0;
        }
    }
}
