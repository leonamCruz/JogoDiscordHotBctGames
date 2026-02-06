package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.Ejaculate;

import java.time.LocalDateTime;
import java.util.Set;

public interface EjaculateRepository extends JpaRepository<Ejaculate, Long> {
    Set<Ejaculate> findByWhenWillItHappenBefore(LocalDateTime whenWillItHappenBefore);

    Set<Ejaculate> findByNotified(boolean notified);
}
