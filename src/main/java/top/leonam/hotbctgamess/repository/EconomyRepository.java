package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.Cacheable;
import top.leonam.hotbctgamess.model.entity.Economy;

import java.util.List;

@Repository
public interface EconomyRepository extends JpaRepository<Economy, Long> {
    @Cacheable(cacheNames = "economyByDiscordId", key = "#playerIdentityDiscordId")
    Economy findByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
    List<Economy> findTop10ByOrderByMoneyDesc();
}
