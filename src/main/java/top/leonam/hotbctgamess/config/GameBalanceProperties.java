package top.leonam.hotbctgamess.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "game")
public class GameBalanceProperties {

    private final Work work = new Work();
    private final Crime crime = new Crime();
    private final Mining mining = new Mining();
    private final Energy energy = new Energy();
    private final Tax tax = new Tax();
    private final Btc btc = new Btc();
    private final Faculdade faculdade = new Faculdade();
    private final Roubo roubo = new Roubo();
    private final Prison prison = new Prison();
    private final Ranking ranking = new Ranking();
    private final Level level = new Level();
    private final Store store = new Store();

    public Work getWork() {
        return work;
    }

    public Crime getCrime() {
        return crime;
    }

    public Mining getMining() {
        return mining;
    }

    public Energy getEnergy() {
        return energy;
    }

    public Tax getTax() {
        return tax;
    }

    public Btc getBtc() {
        return btc;
    }

    public Faculdade getFaculdade() {
        return faculdade;
    }

    public Roubo getRoubo() {
        return roubo;
    }

    public Prison getPrison() {
        return prison;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public Level getLevel() {
        return level;
    }

    public Store getStore() {
        return store;
    }

    public static class Work {
        private final WorkItem ifood = new WorkItem();
        private final WorkItem uber = new WorkItem();
        private final WorkItem estoque = new WorkItem();
        private final WorkItem garcom = new WorkItem();
        private final WorkItem pedreiro = new WorkItem();
        private double faculdadeMultiplier;

        public WorkItem getIfood() {
            return ifood;
        }

        public WorkItem getUber() {
            return uber;
        }

        public WorkItem getEstoque() {
            return estoque;
        }

        public WorkItem getGarcom() {
            return garcom;
        }

        public WorkItem getPedreiro() {
            return pedreiro;
        }

        public double getFaculdadeMultiplier() {
            return faculdadeMultiplier;
        }

        public void setFaculdadeMultiplier(double faculdadeMultiplier) {
            this.faculdadeMultiplier = faculdadeMultiplier;
        }
    }

    public static class Crime {
        private final CrimeItem cc = new CrimeItem();
        private final CrimeItem trafico = new CrimeItem();
        private final CrimeItem sequestro = new CrimeItem();
        private final CrimeItem hackear = new CrimeItem();
        private final CrimeItem laranja = new CrimeItem();
        private final CrimeItem bet = new CrimeItem();

        public CrimeItem getCc() {
            return cc;
        }

        public CrimeItem getTrafico() {
            return trafico;
        }

        public CrimeItem getSequestro() {
            return sequestro;
        }

        public CrimeItem getHackear() {
            return hackear;
        }

        public CrimeItem getLaranja() {
            return laranja;
        }

        public CrimeItem getBet() {
            return bet;
        }
    }

    public static class WorkItem {
        private int gainMin;
        private int gainMax;
        private int cooldown;
        private int levelMin;
        private long xp;

        public int getGainMin() {
            return gainMin;
        }

        public void setGainMin(int gainMin) {
            this.gainMin = gainMin;
        }

        public int getGainMax() {
            return gainMax;
        }

        public void setGainMax(int gainMax) {
            this.gainMax = gainMax;
        }

        public int getCooldown() {
            return cooldown;
        }

        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }

        public int getLevelMin() {
            return levelMin;
        }

        public void setLevelMin(int levelMin) {
            this.levelMin = levelMin;
        }

        public long getXp() {
            return xp;
        }

        public void setXp(long xp) {
            this.xp = xp;
        }
    }

    public static class CrimeItem {
        private int gainMin;
        private int gainMax;
        private int cooldown;
        private int levelMin;
        private long xp;
        private int prisonChance;

        public int getGainMin() {
            return gainMin;
        }

        public void setGainMin(int gainMin) {
            this.gainMin = gainMin;
        }

        public int getGainMax() {
            return gainMax;
        }

        public void setGainMax(int gainMax) {
            this.gainMax = gainMax;
        }

        public int getCooldown() {
            return cooldown;
        }

        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }

        public int getLevelMin() {
            return levelMin;
        }

        public void setLevelMin(int levelMin) {
            this.levelMin = levelMin;
        }

        public long getXp() {
            return xp;
        }

        public void setXp(long xp) {
            this.xp = xp;
        }

        public int getPrisonChance() {
            return prisonChance;
        }

        public void setPrisonChance(int prisonChance) {
            this.prisonChance = prisonChance;
        }
    }

    public static class Mining {
        private long minEnergyPerRound;
        private long xp;
        private long levelMin;

        public long getMinEnergyPerRound() {
            return minEnergyPerRound;
        }

        public void setMinEnergyPerRound(long minEnergyPerRound) {
            this.minEnergyPerRound = minEnergyPerRound;
        }

        public long getXp() {
            return xp;
        }

        public void setXp(long xp) {
            this.xp = xp;
        }

        public long getLevelMin() {
            return levelMin;
        }

        public void setLevelMin(long levelMin) {
            this.levelMin = levelMin;
        }
    }

    public static class Energy {
        private java.math.BigDecimal dailyCost;
        private long dailyBase;
        private long extraPack;
        private java.math.BigDecimal extraPackBaseCost;
        private java.math.BigDecimal extraPackAsicSurcharge;
        private double gatoSuccessChance;
        private int gatoEnergyPacks;
        private double gatoLossPercent;

        public java.math.BigDecimal getDailyCost() {
            return dailyCost;
        }

        public void setDailyCost(java.math.BigDecimal dailyCost) {
            this.dailyCost = dailyCost;
        }

        public long getDailyBase() {
            return dailyBase;
        }

        public void setDailyBase(long dailyBase) {
            this.dailyBase = dailyBase;
        }

        public long getExtraPack() {
            return extraPack;
        }

        public void setExtraPack(long extraPack) {
            this.extraPack = extraPack;
        }

        public java.math.BigDecimal getExtraPackBaseCost() {
            return extraPackBaseCost;
        }

        public void setExtraPackBaseCost(java.math.BigDecimal extraPackBaseCost) {
            this.extraPackBaseCost = extraPackBaseCost;
        }

        public java.math.BigDecimal getExtraPackAsicSurcharge() {
            return extraPackAsicSurcharge;
        }

        public void setExtraPackAsicSurcharge(java.math.BigDecimal extraPackAsicSurcharge) {
            this.extraPackAsicSurcharge = extraPackAsicSurcharge;
        }

        public double getGatoSuccessChance() {
            return gatoSuccessChance;
        }

        public void setGatoSuccessChance(double gatoSuccessChance) {
            this.gatoSuccessChance = gatoSuccessChance;
        }

        public int getGatoEnergyPacks() {
            return gatoEnergyPacks;
        }

        public void setGatoEnergyPacks(int gatoEnergyPacks) {
            this.gatoEnergyPacks = gatoEnergyPacks;
        }

        public double getGatoLossPercent() {
            return gatoLossPercent;
        }

        public void setGatoLossPercent(double gatoLossPercent) {
            this.gatoLossPercent = gatoLossPercent;
        }
    }

    public static class Tax {
        private java.math.BigDecimal rate;
        private java.math.BigDecimal fineRate;
        private double evasionChance;
        private java.math.BigDecimal threshold;

        public java.math.BigDecimal getRate() {
            return rate;
        }

        public void setRate(java.math.BigDecimal rate) {
            this.rate = rate;
        }

        public java.math.BigDecimal getFineRate() {
            return fineRate;
        }

        public void setFineRate(java.math.BigDecimal fineRate) {
            this.fineRate = fineRate;
        }

        public double getEvasionChance() {
            return evasionChance;
        }

        public void setEvasionChance(double evasionChance) {
            this.evasionChance = evasionChance;
        }

        public java.math.BigDecimal getThreshold() {
            return threshold;
        }

        public void setThreshold(java.math.BigDecimal threshold) {
            this.threshold = threshold;
        }
    }

    public static class Btc {
        private java.math.BigDecimal basePrice;
        private java.math.BigDecimal minPrice;
        private java.math.BigDecimal miningIncreasePerBtc;
        private java.math.BigDecimal sellDecreasePerBtc;
        private int sellLevelMin;

        public java.math.BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(java.math.BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public java.math.BigDecimal getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(java.math.BigDecimal minPrice) {
            this.minPrice = minPrice;
        }

        public java.math.BigDecimal getMiningIncreasePerBtc() {
            return miningIncreasePerBtc;
        }

        public void setMiningIncreasePerBtc(java.math.BigDecimal miningIncreasePerBtc) {
            this.miningIncreasePerBtc = miningIncreasePerBtc;
        }

        public java.math.BigDecimal getSellDecreasePerBtc() {
            return sellDecreasePerBtc;
        }

        public void setSellDecreasePerBtc(java.math.BigDecimal sellDecreasePerBtc) {
            this.sellDecreasePerBtc = sellDecreasePerBtc;
        }

        public int getSellLevelMin() {
            return sellLevelMin;
        }

        public void setSellLevelMin(int sellLevelMin) {
            this.sellLevelMin = sellLevelMin;
        }
    }

    public static class Faculdade {
        private int cooldownSeconds;
        private java.math.BigDecimal price;
        private double successChance;

        public int getCooldownSeconds() {
            return cooldownSeconds;
        }

        public void setCooldownSeconds(int cooldownSeconds) {
            this.cooldownSeconds = cooldownSeconds;
        }

        public java.math.BigDecimal getPrice() {
            return price;
        }

        public void setPrice(java.math.BigDecimal price) {
            this.price = price;
        }

        public double getSuccessChance() {
            return successChance;
        }

        public void setSuccessChance(double successChance) {
            this.successChance = successChance;
        }
    }

    public static class Roubo {
        private int dailyLimit;
        private double prisonChance;
        private long xpPenalty;
        private int minPercent;
        private int maxPercent;
        private double moneyPenaltyRate;

        public int getDailyLimit() {
            return dailyLimit;
        }

        public void setDailyLimit(int dailyLimit) {
            this.dailyLimit = dailyLimit;
        }

        public double getPrisonChance() {
            return prisonChance;
        }

        public void setPrisonChance(double prisonChance) {
            this.prisonChance = prisonChance;
        }

        public long getXpPenalty() {
            return xpPenalty;
        }

        public void setXpPenalty(long xpPenalty) {
            this.xpPenalty = xpPenalty;
        }

        public int getMinPercent() {
            return minPercent;
        }

        public void setMinPercent(int minPercent) {
            this.minPercent = minPercent;
        }

        public int getMaxPercent() {
            return maxPercent;
        }

        public void setMaxPercent(int maxPercent) {
            this.maxPercent = maxPercent;
        }

        public double getMoneyPenaltyRate() {
            return moneyPenaltyRate;
        }

        public void setMoneyPenaltyRate(double moneyPenaltyRate) {
            this.moneyPenaltyRate = moneyPenaltyRate;
        }
    }

    public static class Prison {
        private double bailRate;

        public double getBailRate() {
            return bailRate;
        }

        public void setBailRate(double bailRate) {
            this.bailRate = bailRate;
        }
    }

    public static class Ranking {
        private double moneyWeight;
        private double btcWeight;
        private double jobWeight;
        private double crimeWeight;
        private double productWeight;

        public double getMoneyWeight() {
            return moneyWeight;
        }

        public void setMoneyWeight(double moneyWeight) {
            this.moneyWeight = moneyWeight;
        }

        public double getBtcWeight() {
            return btcWeight;
        }

        public void setBtcWeight(double btcWeight) {
            this.btcWeight = btcWeight;
        }

        public double getJobWeight() {
            return jobWeight;
        }

        public void setJobWeight(double jobWeight) {
            this.jobWeight = jobWeight;
        }

        public double getCrimeWeight() {
            return crimeWeight;
        }

        public void setCrimeWeight(double crimeWeight) {
            this.crimeWeight = crimeWeight;
        }

        public double getProductWeight() {
            return productWeight;
        }

        public void setProductWeight(double productWeight) {
            this.productWeight = productWeight;
        }
    }

    public static class Level {
        private long xpBase;

        public long getXpBase() {
            return xpBase;
        }

        public void setXpBase(long xpBase) {
            this.xpBase = xpBase;
        }
    }

    public static class Store {
        private java.util.List<StoreItem> items = new java.util.ArrayList<>();

        public java.util.List<StoreItem> getItems() {
            return items;
        }

        public void setItems(java.util.List<StoreItem> items) {
            this.items = items;
        }
    }

    public static class StoreItem {
        private int id;
        private java.math.BigDecimal price;
        private java.math.BigDecimal btcPerMineBonus;
        private Long energyCostBonus;
        private Long dailyEnergyBonus;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public java.math.BigDecimal getPrice() {
            return price;
        }

        public void setPrice(java.math.BigDecimal price) {
            this.price = price;
        }

        public java.math.BigDecimal getBtcPerMineBonus() {
            return btcPerMineBonus;
        }

        public void setBtcPerMineBonus(java.math.BigDecimal btcPerMineBonus) {
            this.btcPerMineBonus = btcPerMineBonus;
        }

        public Long getEnergyCostBonus() {
            return energyCostBonus;
        }

        public void setEnergyCostBonus(Long energyCostBonus) {
            this.energyCostBonus = energyCostBonus;
        }

        public Long getDailyEnergyBonus() {
            return dailyEnergyBonus;
        }

        public void setDailyEnergyBonus(Long dailyEnergyBonus) {
            this.dailyEnergyBonus = dailyEnergyBonus;
        }
    }
}
