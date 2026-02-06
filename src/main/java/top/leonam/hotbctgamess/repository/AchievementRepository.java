package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
