package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.DailyStats;

import java.time.LocalDate;

public interface DailyStatsRepository extends JpaRepository<DailyStats,Long> {
    DailyStats getFirstByDateOrderByIdDesc(LocalDate date);
}
