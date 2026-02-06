package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.leonam.hotbctgamess.model.entity.Identity;

public interface IdentityRepository extends JpaRepository<Identity, Long> {
}
