package top.leonam.hotbctgamess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leonam.hotbctgamess.model.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
    long countByPlayer_Identity_DiscordId(Long playerIdentityDiscordId);
}
