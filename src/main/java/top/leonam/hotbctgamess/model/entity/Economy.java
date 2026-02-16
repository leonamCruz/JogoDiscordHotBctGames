package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Economy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "economy")
    private Player player;

    @Column(precision = 20, scale = 2)
    private BigDecimal money;
    @Column(precision = 20, scale = 10)
    private BigDecimal btc;
    private Long energy;
    private LocalDate lastEnergyPayment;

    @PrePersist
    public void setup(){
        if (money == null) {
            money = BigDecimal.ZERO;
        }
        if (btc == null) {
            btc = BigDecimal.ZERO;
        }
        if (energy == null) {
            energy = 0L;
        }
    }
}
