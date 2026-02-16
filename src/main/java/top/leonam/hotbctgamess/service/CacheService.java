package top.leonam.hotbctgamess.service;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheService {

    private static final List<String> PLAYER_CACHES = List.of(
            "economyByDiscordId",
            "jobByDiscordId",
            "levelByDiscordId",
            "playerByDiscordId",
            "productsByDiscordId",
            "productCountByDiscordId"
    );

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictPlayer(Long discordId) {
        if (discordId == null) {
            return;
        }
        for (String cacheName : PLAYER_CACHES) {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(discordId);
            }
        }
    }
}
