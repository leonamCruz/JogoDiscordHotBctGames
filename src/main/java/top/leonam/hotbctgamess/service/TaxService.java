package top.leonam.hotbctgamess.service;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class TaxService {

    private final Random random;
    private final GameBalanceProperties.Tax balance;

    public TaxService(Random random, GameBalanceProperties balanceProperties) {
        this.random = random;
        this.balance = balanceProperties.getTax();
    }

    public BigDecimal calculateTax(BigDecimal amount) {
        return amount.multiply(balance.getRate());
    }

    public BigDecimal calculateFine(BigDecimal amount) {
        return amount.multiply(balance.getFineRate());
    }

    public boolean isCaught() {
        return random.nextDouble() < balance.getEvasionChance();
    }

    public BigDecimal getThreshold() {
        return balance.getThreshold();
    }

    public boolean isTaxable(BigDecimal amount) {
        return amount.compareTo(balance.getThreshold()) > 0;
    }

    public BigDecimal getRate() {
        return balance.getRate();
    }

    public BigDecimal getFineRate() {
        return balance.getFineRate();
    }
}
