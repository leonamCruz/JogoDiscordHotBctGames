package top.leonam.hotbctgamess.service;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Product;
import top.leonam.hotbctgamess.model.enums.StoreProduct;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class EnergyService {

    private final EconomyRepository economyRepository;
    private final ProductRepository productRepository;
    private final GameBalanceProperties.Energy balance;

    public EnergyService(
            EconomyRepository economyRepository,
            ProductRepository productRepository,
            GameBalanceProperties balanceProperties
    ) {
        this.economyRepository = economyRepository;
        this.productRepository = productRepository;
        this.balance = balanceProperties.getEnergy();
    }

    public EnergyStatus getStatus(Long discordId) {
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        List<Product> products = productRepository.findByPlayer_Identity_DiscordId(discordId);
        long dailyBonus = getDailyEnergyBonus(products);
        long asicCount = countAsics(products);
        LocalDate today = LocalDate.now();
        boolean alreadyPaid = today.equals(economy.getLastEnergyPayment());
        return new EnergyStatus(
                economy,
                products,
                dailyBonus,
                asicCount,
                alreadyPaid,
                safeMoney(economy.getMoney()),
                safeEnergy(economy.getEnergy())
        );
    }

    public EnergyPurchaseResult purchaseEnergy(Long discordId, int extraPacks, boolean allowDaily) {
        EnergyStatus status = getStatus(discordId);
        boolean dailyPaidNow = false;

        BigDecimal dailyCost = BigDecimal.ZERO;
        long dailyEnergy = 0L;
        if (!status.alreadyPaid && allowDaily) {
            dailyCost = balance.getDailyCost();
            dailyEnergy = balance.getDailyBase() + status.dailyBonus;
        }

        BigDecimal extraCost = getExtraPackCost(status.asicCount)
                .multiply(BigDecimal.valueOf(extraPacks));
        BigDecimal totalCost = dailyCost.add(extraCost);

        if (status.money.compareTo(totalCost) < 0) {
            return EnergyPurchaseResult.insufficient(totalCost, status.currentEnergy);
        }

        long energyToAdd = dailyEnergy + (balance.getExtraPack() * extraPacks);
        Economy economy = status.economy;
        economy.setMoney(status.money.subtract(totalCost));
        economy.setEnergy(status.currentEnergy + energyToAdd);
        if (!status.alreadyPaid && allowDaily) {
            economy.setLastEnergyPayment(LocalDate.now());
            dailyPaidNow = true;
        }
        economyRepository.save(economy);

        return EnergyPurchaseResult.success(totalCost, energyToAdd, economy.getEnergy(), dailyPaidNow);
    }

    public BigDecimal getExtraPackCost(long asicCount) {
        return balance.getExtraPackBaseCost()
                .add(balance.getExtraPackAsicSurcharge().multiply(BigDecimal.valueOf(asicCount)));
    }

    public long getExtraPackSize() {
        return balance.getExtraPack();
    }

    public long getDailyBaseEnergy() {
        return balance.getDailyBase();
    }

    public double getGatoSuccessChance() {
        return balance.getGatoSuccessChance();
    }

    public int getGatoEnergyPacks() {
        return balance.getGatoEnergyPacks();
    }

    public double getGatoLossPercent() {
        return balance.getGatoLossPercent();
    }

    private long getDailyEnergyBonus(List<Product> products) {
        long bonus = 0L;
        for (Product product : products) {
            Integer storeId = product.getStoreProductId();
            if (storeId == null) {
                continue;
            }
            StoreProduct storeProduct = StoreProduct.fromId(storeId);
            if (storeProduct != null) {
                bonus += storeProduct.getDailyEnergyBonus();
            }
        }
        return bonus;
    }

    private long countAsics(List<Product> products) {
        long count = 0L;
        for (Product product : products) {
            Integer storeId = product.getStoreProductId();
            if (storeId == null) {
                continue;
            }
            StoreProduct storeProduct = StoreProduct.fromId(storeId);
            if (storeProduct != null && storeProduct.isAsic()) {
                count++;
            }
        }
        return count;
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private long safeEnergy(Long value) {
        return value == null ? 0L : value;
    }

    public record EnergyStatus(
            Economy economy,
            List<Product> products,
            long dailyBonus,
            long asicCount,
            boolean alreadyPaid,
            BigDecimal money,
            long currentEnergy
    ) {
    }

    public record EnergyPurchaseResult(
            boolean success,
            BigDecimal totalCost,
            long energyAdded,
            long currentEnergy,
            boolean dailyPaidNow
    ) {
        public static EnergyPurchaseResult insufficient(BigDecimal totalCost, long currentEnergy) {
            return new EnergyPurchaseResult(false, totalCost, 0L, currentEnergy, false);
        }

        public static EnergyPurchaseResult success(
                BigDecimal totalCost,
                long energyAdded,
                long currentEnergy,
                boolean dailyPaidNow
        ) {
            return new EnergyPurchaseResult(true, totalCost, energyAdded, currentEnergy, dailyPaidNow);
        }
    }
}
