package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "egg")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Egg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "egg_id", nullable = false ,unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;

    @Column(name = "inflamed", nullable = false)
    private Boolean inflamed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @PrePersist
    public void prePersist() {
        remainingQuantity = 3;
        inflamed = false;
    }

}
