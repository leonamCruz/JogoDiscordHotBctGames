package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class BtcMarket {
    @Id
    private Long id;
    private BigDecimal price;

    @PrePersist
    public void setup() {
        if (price == null) {
            price = new BigDecimal("100000");
        }
    }
}
