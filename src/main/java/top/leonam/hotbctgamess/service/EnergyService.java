package top.leonam.hotbctgamess.service;

import org.springframework.stereotype.Service;
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

    public static final BigDecimal DAILY_COST = new BigDecimal("80");
    public static final long DAILY_ENERGY_BASE = 100L;
    public static final long EXTRA_ENERGY_PACK = 25L;
    public static final BigDecimal EXTRA_PACK_BASE_COST = new BigDecimal("40");
    public static final BigDecimal EXTRA_PACK_ASIC_SURCHARGE = new BigDecimal("20");

    private final EconomyRepository economyRepository;
    private final ProductRepository productRepository;

    public EnergyService(EconomyRepository economyRepository, ProductRepository productRepository) {
        this.economyRepository = economyRepository;
        this.productRepository = productRepository;
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
            dailyCost = DAILY_COST;
            dailyEnergy = DAILY_ENERGY_BASE + status.dailyBonus;
        }

        BigDecimal extraCost = getExtraPackCost(status.asicCount)
                .multiply(BigDecimal.valueOf(extraPacks));
        BigDecimal totalCost = dailyCost.add(extraCost);

        if (status.money.compareTo(totalCost) < 0) {
            return EnergyPurchaseResult.insufficient(totalCost, status.currentEnergy);
        }

        long energyToAdd = dailyEnergy + (EXTRA_ENERGY_PACK * extraPacks);
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
        return EXTRA_PACK_BASE_COST.add(EXTRA_PACK_ASIC_SURCHARGE.multiply(BigDecimal.valueOf(asicCount)));
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
