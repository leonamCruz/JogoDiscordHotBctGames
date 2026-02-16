package top.leonam.hotbctgamess.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class TaxService {

    public static final BigDecimal TAX_RATE = new BigDecimal("0.25");
    public static final BigDecimal FINE_RATE = new BigDecimal("0.10");
    public static final double EVASION_CHANCE = 0.23;
    public static final BigDecimal TAX_THRESHOLD = new BigDecimal("5000");

    private final Random random;

    public TaxService(Random random) {
        this.random = random;
    }

    public BigDecimal calculateTax(BigDecimal amount) {
        return amount.multiply(TAX_RATE);
    }

    public BigDecimal calculateFine(BigDecimal amount) {
        return amount.multiply(FINE_RATE);
    }

    public boolean isCaught() {
        return random.nextDouble() < EVASION_CHANCE;
    }
}
