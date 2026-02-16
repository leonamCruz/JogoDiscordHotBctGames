package top.leonam.hotbctgamess.service;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.BtcMarket;
import top.leonam.hotbctgamess.repository.BtcMarketRepository;

import java.math.BigDecimal;

@Service
public class BtcMarketService {

    private static final BigDecimal BASE_PRICE = new BigDecimal("50000");
    private static final BigDecimal MIN_PRICE = new BigDecimal("500");
    private static final BigDecimal MINING_INCREASE_PER_BTC = new BigDecimal("10000");
    private static final BigDecimal SELL_DECREASE_PER_BTC = new BigDecimal("20000");

    private final BtcMarketRepository repository;

    public BtcMarketService(BtcMarketRepository repository) {
        this.repository = repository;
    }

    public synchronized BigDecimal getCurrentPrice() {
        return getOrCreate().getPrice();
    }

    public synchronized void applyMining(BigDecimal btcMined) {
        if (btcMined == null || btcMined.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BtcMarket market = getOrCreate();
        BigDecimal delta = btcMined.multiply(MINING_INCREASE_PER_BTC);
        market.setPrice(market.getPrice().add(delta));
        repository.save(market);
    }

    public synchronized void applySell(BigDecimal btcSold) {
        if (btcSold == null || btcSold.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BtcMarket market = getOrCreate();
        BigDecimal delta = btcSold.multiply(SELL_DECREASE_PER_BTC);
        BigDecimal next = market.getPrice().subtract(delta);
        if (next.compareTo(MIN_PRICE) < 0) {
            next = MIN_PRICE;
        }
        market.setPrice(next);
        repository.save(market);
    }

    private BtcMarket getOrCreate() {
        return repository.findById(1L).orElseGet(() -> {
            BtcMarket market = new BtcMarket();
            market.setId(1L);
            market.setPrice(BASE_PRICE);
            return repository.save(market);
        });
    }
}
