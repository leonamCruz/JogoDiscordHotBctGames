package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(indexes = {
        @Index(name = "idx_product_player_id", columnList = "player_id")
})
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    @Column(columnDefinition = "TEXT")
    private String description;
    private BigDecimal price;
    private Integer storeProductId;
}
