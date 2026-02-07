package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.Player;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByIdentity_DiscordId(Long identityDiscordId);

}
