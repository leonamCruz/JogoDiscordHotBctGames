package top.leonam.hotbctgamess.model.enums;

import java.math.BigDecimal;
import java.util.Arrays;

public enum StoreProduct {
    GPU_GTX_1060(
            1,
            "GPU GTX 1060",
            "GPU basica para mineracao. ⚡",
            new BigDecimal("1500"),
            new BigDecimal("0.00002"),
            6,
            0
    ),
    GPU_RX_580(
            2,
            "GPU RX 580",
            "GPU intermediaria para mineracao. 🔧",
            new BigDecimal("2500"),
            new BigDecimal("0.00003"),
            8,
            0
    ),
    GPU_RTX_2060(
            3,
            "GPU RTX 2060",
            "GPU forte para mineracao. 🚀",
            new BigDecimal("4000"),
            new BigDecimal("0.00004"),
            10,
            0
    ),
    ASIC_S9(
            4,
            "ASIC Antminer S9",
            "ASIC de entrada com bom desempenho. 🧊",
            new BigDecimal("12000"),
            new BigDecimal("0.00008"),
            14,
            0
    ),
    ASIC_S17(
            5,
            "ASIC Antminer S17",
            "ASIC eficiente para mineracao pesada. 💥",
            new BigDecimal("20000"),
            new BigDecimal("0.00012"),
            18,
            0
    ),
    ASIC_S19(
            6,
            "ASIC Antminer S19",
            "ASIC top de linha para mineracao. 🏆",
            new BigDecimal("35000"),
            new BigDecimal("0.00018"),
            22,
            0
    ),
    POWER_SUPPLY(
            7,
            "Fonte 80 Plus Gold",
            "Reduz o gasto de energia por rodada. ⚡",
            new BigDecimal("1200"),
            BigDecimal.ZERO,
            -2,
            0
    ),
    INDUSTRIAL_COOLING(
            8,
            "Refrigeracao Industrial",
            "Diminui o gasto de energia com maquinas quentes. ❄️",
            new BigDecimal("5000"),
            BigDecimal.ZERO,
            -3,
            0
    ),
    MINING_RACK(
            9,
            "Rack de Mineracao",
            "Aumenta a energia diaria disponivel. 🧰",
            new BigDecimal("3000"),
            BigDecimal.ZERO,
            0,
            25
    ),
    DIESEL_GENERATOR(
            10,
            "Gerador a Diesel",
            "Aumenta bastante a energia diaria disponivel. ⛽",
            new BigDecimal("12000"),
            BigDecimal.ZERO,
            0,
            60
    );

    private final int id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final BigDecimal btcPerMineBonus;
    private final long energyCostBonus;
    private final long dailyEnergyBonus;

    StoreProduct(
            int id,
            String name,
            String description,
            BigDecimal price,
            BigDecimal btcPerMineBonus,
            long energyCostBonus,
            long dailyEnergyBonus
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.btcPerMineBonus = btcPerMineBonus;
        this.energyCostBonus = energyCostBonus;
        this.dailyEnergyBonus = dailyEnergyBonus;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getBtcPerMineBonus() {
        return btcPerMineBonus;
    }

    public long getEnergyCostBonus() {
        return energyCostBonus;
    }

    public long getDailyEnergyBonus() {
        return dailyEnergyBonus;
    }

    public boolean isAsic() {
        return switch (this) {
            case ASIC_S9, ASIC_S17, ASIC_S19 -> true;
            default -> false;
        };
    }

    public static StoreProduct fromId(int id) {
        return Arrays.stream(values())
                .filter(product -> product.id == id)
                .findFirst()
                .orElse(null);
    }

    public String toDisplayLine() {
        String energyCostText = energyCostBonus == 0
                ? "0"
                : (energyCostBonus > 0 ? "+" + energyCostBonus : String.valueOf(energyCostBonus));
        return """
                %d - %s
                Preco: R$%.2f
                Descricao: %s
                BTC/rodada: +%.5f
                kWh/rodada: %s
                kWh/dia: +%d
                """.formatted(id, name, price, description, btcPerMineBonus, energyCostText, dailyEnergyBonus).trim();
    }
}
