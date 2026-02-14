package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.model.entity.Economy;

@Repository
public interface EconomyRepository extends JpaRepository<Economy, Long> {
    Economy findByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
}
