package top.leonam.hotbctgamess.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.model.entity.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level,Long> {
    @Cacheable(cacheNames = "levelByDiscordId", key = "#playerIdentityDiscordId")
    Level findByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
}
