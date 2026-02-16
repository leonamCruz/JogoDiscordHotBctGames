package top.leonam.hotbctgamess.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.model.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Cacheable(cacheNames = "productsByDiscordId", key = "#playerIdentityDiscordId")
    List<Product> findByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
    @Cacheable(cacheNames = "productCountByDiscordId", key = "#playerIdentityDiscordId")
    long countByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
}
