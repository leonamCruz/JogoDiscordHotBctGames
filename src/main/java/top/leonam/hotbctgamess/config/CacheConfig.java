package top.leonam.hotbctgamess.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "crimes",
                "crimeByName"
        );

        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(1_000)
                        .expireAfterWrite(1, TimeUnit.HOURS)
        );

        return manager;
    }
}
