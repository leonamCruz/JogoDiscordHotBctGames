package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.PrisonHistory;

public interface PrisonHistoryRepository extends JpaRepository<PrisonHistory, Long> {
}
