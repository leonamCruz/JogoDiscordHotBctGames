package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.Cacheable;
import top.leonam.hotbctgamess.model.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {
    @Cacheable(cacheNames = "jobByDiscordId", key = "#playerIdentityDiscordId")
    Job findByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
}
