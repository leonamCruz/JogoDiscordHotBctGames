package top.leonam.hotbctgamess.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import top.leonam.hotbctgamess.model.enums.StoreProduct;

import java.util.HashMap;
import java.util.Map;

@Component
public class StoreProductBalanceInitializer {

    private final GameBalanceProperties properties;

    public StoreProductBalanceInitializer(GameBalanceProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void applyStoreOverrides() {
        Map<Integer, StoreProduct.StoreOverride> overrides = new HashMap<>();
        for (GameBalanceProperties.StoreItem item : properties.getStore().getItems()) {
            if (item.getId() <= 0) {
                continue;
            }
            overrides.put(
                    item.getId(),
                    new StoreProduct.StoreOverride(
                            item.getPrice(),
                            item.getBtcPerMineBonus(),
                            item.getEnergyCostBonus(),
                            item.getDailyEnergyBonus()
                    )
            );
        }
        StoreProduct.setOverrides(overrides);
    }
}
