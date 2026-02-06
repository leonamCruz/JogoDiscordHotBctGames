package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import top.leonam.hotbctgamess.model.enums.EjaculateStatus;

import java.time.LocalDateTime;
@Entity(name = "ejaculate")
@Table(
        name = "ejaculate",
        indexes = {
                @Index(
                        name = "idx_ejaculate_when_will_it_happen",
                        columnList = "when_will_it_happen"
                )
        }
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ejaculate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ejaculate_id", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "of_player_id", nullable = false)
    private Player of;

    @ManyToOne
    @JoinColumn(name = "from_player_id", nullable = false)
    private Player from;

    @Column(name = "channel_id",nullable = false)
    private Long idChannel;

    @Column(name = "when_will_it_happen", nullable = false)
    private LocalDateTime whenWillItHappen;

    @Enumerated(EnumType.STRING)
    @Column(name = "ejaculate_satus", nullable = false)
    private EjaculateStatus status;

    @Column(name = "notified", nullable = false)
    private boolean notified = false;

}
