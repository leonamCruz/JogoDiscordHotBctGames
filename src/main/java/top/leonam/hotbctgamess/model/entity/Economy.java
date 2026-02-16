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
        sanitize();
    }

    @PreUpdate
    public void preUpdate() {
        sanitize();
    }

    private void sanitize() {
        if (money.compareTo(BigDecimal.ZERO) < 0) {
            money = BigDecimal.ZERO;
        }
        if (btc.compareTo(BigDecimal.ZERO) < 0) {
            btc = BigDecimal.ZERO;
        }
        if (energy < 0) {
            energy = 0L;
        }
    }
}
