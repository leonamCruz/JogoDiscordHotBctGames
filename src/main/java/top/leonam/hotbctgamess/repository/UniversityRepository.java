package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.model.entity.University;

@Repository
public interface UniversityRepository extends JpaRepository<University,Long> {
    University findByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
}
