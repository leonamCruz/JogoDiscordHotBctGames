package top.leonam.hotbctgamess.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import top.leonam.hotbctgamess.model.entity.Level;

@Component
public class GameBalanceInitializer {

    private final GameBalanceProperties properties;

    public GameBalanceInitializer(GameBalanceProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void applyLevelBalance() {
        Level.setXpBase(properties.getLevel().getXpBase());
    }
}
