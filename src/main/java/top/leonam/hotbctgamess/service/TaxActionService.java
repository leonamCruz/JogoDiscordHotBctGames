package top.leonam.hotbctgamess.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class TaxActionService {

    private static final String CACHE_NAME = "taxActions";

    private final CacheManager cacheManager;

    public TaxActionService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void store(long userId, TaxAction action) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(key(userId, action.type()), action);
        }
    }

    public TaxAction consume(long userId, String type) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            return null;
        }
        String key = key(userId, type);
        TaxAction action = cache.get(key, TaxAction.class);
        if (action != null) {
            cache.evict(key);
        }
        return action;
    }

    private String key(long userId, String type) {
        return userId + ":" + type;
    }

    public record TaxAction(String type, String payload) {
    }
}
