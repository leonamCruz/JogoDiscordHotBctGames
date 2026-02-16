package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.model.entity.BtcMarket;

@Repository
public interface BtcMarketRepository extends JpaRepository<BtcMarket, Long> {
}
