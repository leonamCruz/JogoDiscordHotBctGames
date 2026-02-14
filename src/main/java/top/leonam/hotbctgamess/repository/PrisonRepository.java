package top.leonam.hotbctgamess.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.model.entity.Prison;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PrisonRepository extends JpaRepository<Prison,Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Prison p set p.active = true, p.lastPrison = :datePrison where p.player.identity.discordId = :discordId")
    void prender(@Param("discordId") Long discordId, @Param("datePrison") LocalDateTime datePrison);

    @Transactional
    Prison findByPlayer_Identity_DiscordId(Long discordId);
}
