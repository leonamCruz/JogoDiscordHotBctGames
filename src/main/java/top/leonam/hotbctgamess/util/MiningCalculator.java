package top.leonam.hotbctgamess.util;

import top.leonam.hotbctgamess.model.entity.Product;
import top.leonam.hotbctgamess.model.enums.StoreProduct;

import java.math.BigDecimal;
import java.util.List;

public final class MiningCalculator {

    private MiningCalculator() {
    }

    public static MiningTotals calculate(List<Product> products) {
        long energyTotal = 0L;
        BigDecimal btcTotal = BigDecimal.ZERO;
        for (Product product : products) {
            Integer storeId = product.getStoreProductId();
            if (storeId == null) {
                continue;
            }
            StoreProduct storeProduct = StoreProduct.fromId(storeId);
            if (storeProduct != null) {
                energyTotal += storeProduct.getEnergyCostBonus();
                btcTotal = btcTotal.add(storeProduct.getBtcPerMineBonus());
            }
        }
        return new MiningTotals(btcTotal, energyTotal);
    }

    public record MiningTotals(BigDecimal btcPerRound, long energyPerRound) {
    }
}
