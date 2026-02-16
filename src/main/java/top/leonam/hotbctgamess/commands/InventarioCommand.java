package top.leonam.hotbctgamess.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Product;
import top.leonam.hotbctgamess.model.enums.StoreProduct;
import top.leonam.hotbctgamess.repository.ProductRepository;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class InventarioCommand implements Command {

    private final ProductRepository productRepository;

    public InventarioCommand(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public String name() {
        return ".inventario";
    }

    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        Long discordId = event.getAuthor().getIdLong();
        List<Product> products = productRepository.findByPlayer_Identity_DiscordId(discordId);

        if (products.isEmpty()) {
            return new EmbedBuilder()
                    .setTitle("Inventario vazio 🎒")
                    .setDescription("Compre itens na .loja para minerar melhor. 🛒")
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        Map<StoreProduct, Integer> counts = new EnumMap<>(StoreProduct.class);
        for (Product product : products) {
            Integer storeId = product.getStoreProductId();
            if (storeId == null) {
                continue;
            }
            StoreProduct storeProduct = StoreProduct.fromId(storeId);
            if (storeProduct != null) {
                counts.merge(storeProduct, 1, Integer::sum);
            }
        }

        StringBuilder list = new StringBuilder();
        counts.forEach((product, qty) ->
                list.append("%dx %s\n".formatted(qty, product.getName()))
        );

        String bonus = buildBonus(counts);

        return new EmbedBuilder()
                .setTitle("Seu inventario 🎒")
                .setDescription(list + "\nBonus ativos:\n" + bonus)
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.CYAN)
                .setFooter("HotBctsGames");
    }

    private String buildBonus(Map<StoreProduct, Integer> counts) {
        long energyPerRound = 0L;
        long dailyEnergy = 0L;
        java.math.BigDecimal btcPerRound = java.math.BigDecimal.ZERO;

        for (Map.Entry<StoreProduct, Integer> entry : counts.entrySet()) {
            StoreProduct product = entry.getKey();
            int qty = entry.getValue();
            energyPerRound += product.getEnergyCostBonus() * qty;
            dailyEnergy += product.getDailyEnergyBonus() * qty;
            btcPerRound = btcPerRound.add(product.getBtcPerMineBonus().multiply(java.math.BigDecimal.valueOf(qty)));
        }

        return """
                BTC/rodada: +%.5f 🪙
                kWh/rodada: %+d ⚡
                kWh/dia: +%d 🔋
                """.formatted(btcPerRound, energyPerRound, dailyEnergy);
    }
}
