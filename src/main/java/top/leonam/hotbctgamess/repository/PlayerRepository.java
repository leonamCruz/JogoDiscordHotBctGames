package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.dto.RankingRow;
import top.leonam.hotbctgamess.model.entity.Player;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByIdentity_DiscordId(Long identityDiscordId);

    @Query("""
            select new top.leonam.hotbctgamess.dto.RankingRow(
                i.name,
                i.discordId,
                e.money,
                e.btc,
                j.totalJobs,
                j.totalCrimes,
                l.level,
                count(prod)
            )
            from Player p
            join p.identity i
            join p.economy e
            join p.job j
            join p.level l
            left join p.products prod
            group by i.name, i.discordId, e.money, e.btc, j.totalJobs, j.totalCrimes, l.level
            """)
    List<RankingRow> findRankingRows();
}
