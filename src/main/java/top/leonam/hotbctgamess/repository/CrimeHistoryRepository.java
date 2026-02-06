package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.CrimeHistory;

import java.util.Optional;

public interface CrimeHistoryRepository extends JpaRepository<CrimeHistory, Long> {
    Optional<CrimeHistory> findFirstByPlayer_Identity_DiscordIdOrderByAttemptedAtDesc(Long playerIdentityDiscordId);
}
