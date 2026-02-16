package top.leonam.hotbctgamess.service;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;

import java.math.BigDecimal;

@Service
public class RankingScoreService {

    private final GameBalanceProperties.Ranking balance;

    public RankingScoreService(GameBalanceProperties properties) {
        this.balance = properties.getRanking();
    }

    public double score(
            BigDecimal money,
            BigDecimal btc,
            long jobs,
            long crimes,
            long products
    ) {
        double moneyValue = money == null ? 0.0 : money.doubleValue();
        double btcValue = btc == null ? 0.0 : btc.doubleValue();
        return (moneyValue * balance.getMoneyWeight())
                + (btcValue * balance.getBtcWeight())
                + (jobs * balance.getJobWeight())
                + (crimes * balance.getCrimeWeight())
                + (products * balance.getProductWeight());
    }
}
