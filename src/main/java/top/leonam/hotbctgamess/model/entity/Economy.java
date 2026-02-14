package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class Economy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "economy")
    private Player player;

    private BigDecimal money;
    private BigDecimal btc;

    @PrePersist
    public void setup(){
        money = BigDecimal.ZERO;
        btc = BigDecimal.ZERO;
    }
}
