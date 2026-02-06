package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.Transaction;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Set<Transaction>> findByOriginAccount_Player_Identity_DiscordIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long accountPlayerIdentityDiscordId, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    Optional<Set<Transaction>> findTop5ByOriginAccount_Player_Identity_DiscordIdOrderByCreatedAtDesc(Long originAccountPlayerIdentityDiscordId);
}
