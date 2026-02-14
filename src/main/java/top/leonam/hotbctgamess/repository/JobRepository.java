package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.model.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {
    Job findByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
}
