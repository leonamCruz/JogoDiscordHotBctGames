package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = {
        @Index(name = "idx_identity_discord_id", columnList = "discord_id", unique = true)
})
@Data
@NoArgsConstructor
public class Identity {
    public Identity(String name, Long discordId) {
        this.name = name;
        this.discordId = discordId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "discord_id", nullable = false, unique = true)
    private Long discordId;
}
