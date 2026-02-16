package top.leonam.hotbctgamess.service;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.model.entity.BtcMarket;
import top.leonam.hotbctgamess.repository.BtcMarketRepository;

import java.math.BigDecimal;

@Service
public class BtcMarketService {

    private final BtcMarketRepository repository;
    private final GameBalanceProperties.Btc balance;

    public BtcMarketService(BtcMarketRepository repository, GameBalanceProperties balanceProperties) {
        this.repository = repository;
        this.balance = balanceProperties.getBtc();
    }

    public synchronized BigDecimal getCurrentPrice() {
        return getOrCreate().getPrice();
    }

    public synchronized void applyMining(BigDecimal btcMined) {
        if (btcMined == null || btcMined.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BtcMarket market = getOrCreate();
        BigDecimal delta = btcMined.multiply(balance.getMiningIncreasePerBtc());
        market.setPrice(market.getPrice().add(delta));
        repository.save(market);
    }

    public synchronized void applySell(BigDecimal btcSold) {
        if (btcSold == null || btcSold.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BtcMarket market = getOrCreate();
        BigDecimal delta = btcSold.multiply(balance.getSellDecreasePerBtc());
        BigDecimal next = market.getPrice().subtract(delta);
        if (next.compareTo(balance.getMinPrice()) < 0) {
            next = balance.getMinPrice();
        }
        market.setPrice(next);
        repository.save(market);
    }

    private BtcMarket getOrCreate() {
        return repository.findById(1L).orElseGet(() -> {
            BtcMarket market = new BtcMarket();
            market.setId(1L);
            market.setPrice(balance.getBasePrice());
            return repository.save(market);
        });
    }
}
