package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.Crime;

import java.util.Optional;

public interface CrimeRepository extends JpaRepository<Crime, Long> {
    Optional<Crime> findByName(String name);
}
