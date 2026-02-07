package top.leonam.hotbctgamess.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.model.entity.Egg;
import top.leonam.hotbctgamess.model.entity.Player;

@Repository
public interface EggRepository extends JpaRepository<Egg,Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Egg e set e.remainingQuantity = :value")
    void updateMaxEjaculateForAll(@Param("value")  int value);

    @Modifying
    @Transactional
    @Query("UPDATE Egg e SET e.remainingQuantity = CASE WHEN e.remainingQuantity > 0 THEN e.remainingQuantity - 1 ELSE e.remainingQuantity END, e.inflamed = CASE WHEN e.remainingQuantity = 1 THEN true ELSE e.inflamed END WHERE e.player = :player")
    void updateRemainingQuantitySubtractOne(@Param("player") Player player);

    @Transactional
    @Query("select e.remainingQuantity from Egg e where e.player = :player")
    Integer returnRemainingQuantity(@Param("player") Player player);
}
