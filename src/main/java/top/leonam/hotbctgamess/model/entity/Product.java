package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.leonam.hotbctgamess.model.enums.Rarity;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false ,unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "price", nullable = false, precision = 30, scale = 10)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "rarity", nullable = false, length = 20)
    private Rarity rarity;

    @Column(name = "min_respect_required")
    private Integer minRespectRequired;

    @Column(name = "min_level_required")
    private Integer minLevelRequired;

    @Column(name = "is_illegal")
    private boolean illegal;
}
