package top.leonam.hotbctgamess.util;

import java.math.BigDecimal;

public final class RankingCalculator {

    public static final double MONEY_WEIGHT = 1.0;
    public static final double BTC_WEIGHT = 1_000_000.0;
    public static final double JOB_WEIGHT = 50.0;
    public static final double CRIME_WEIGHT = 80.0;
    public static final double PRODUCT_WEIGHT = 200.0;

    private RankingCalculator() {
    }

    public static double score(
            BigDecimal money,
            BigDecimal btc,
            long jobs,
            long crimes,
            long products
    ) {
        double moneyValue = money == null ? 0.0 : money.doubleValue();
        double btcValue = btc == null ? 0.0 : btc.doubleValue();
        return (moneyValue * MONEY_WEIGHT)
                + (btcValue * BTC_WEIGHT)
                + (jobs * JOB_WEIGHT)
                + (crimes * CRIME_WEIGHT)
                + (products * PRODUCT_WEIGHT);
    }
}
