package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.Prison;

import java.util.Optional;

public interface PrisonRepository extends JpaRepository<Prison, Long> {
    Optional<Prison> findByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
}
