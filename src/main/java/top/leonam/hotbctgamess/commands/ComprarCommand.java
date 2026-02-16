package top.leonam.hotbctgamess.commands;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.model.entity.Product;
import top.leonam.hotbctgamess.model.enums.StoreProduct;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.PlayerRepository;
import top.leonam.hotbctgamess.repository.ProductRepository;
import top.leonam.hotbctgamess.service.TaxService;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;

@Service
public class ComprarCommand implements Command {

    private final EconomyRepository economyRepository;
    private final PlayerRepository playerRepository;
    private final ProductRepository productRepository;
    private final TaxService taxService;

    public ComprarCommand(
            EconomyRepository economyRepository,
            PlayerRepository playerRepository,
            ProductRepository productRepository,
            TaxService taxService
    ) {
        this.economyRepository = economyRepository;
        this.playerRepository = playerRepository;
        this.productRepository = productRepository;
        this.taxService = taxService;
    }

    @Override
    public String name() {
        return ".comprar";
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        String[] parts = event.getMessage().getContentRaw().trim().split("\\s+");
        if (parts.length < 2) {
            return buildUsage(event);
        }

        int productId;
        try {
            productId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            return buildUsage(event);
        }

        StoreProduct storeProduct = StoreProduct.fromId(productId);
        if (storeProduct == null) {
            return new EmbedBuilder()
                    .setTitle("Produto nao encontrado")
                    .setDescription("""
                            Status: Produto invalido
                            Dica: use .loja para ver os produtos
                            """)
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.RED)
                    .setFooter("HotBctsGames");
        }

        Long discordId = event.getAuthor().getIdLong();
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        BigDecimal money = getMoney(economy);

        if (storeProduct.getPrice().compareTo(TaxService.TAX_THRESHOLD) > 0) {
            BigDecimal tax = taxService.calculateTax(storeProduct.getPrice());
            BigDecimal total = storeProduct.getPrice().add(tax);
            return new EmbedBuilder()
                    .setTitle("Imposto necessario")
                    .setDescription("""
                            Produto: %s
                            Preco: R$%.2f
                            Imposto (25%%): R$%.2f
                            Total: R$%.2f
                            Escolha: pagar ou sonegar
                            """.formatted(storeProduct.getName(), storeProduct.getPrice(), tax, total))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        if (money.compareTo(storeProduct.getPrice()) < 0) {
            return new EmbedBuilder()
                    .setTitle("Dinheiro insuficiente")
                    .setDescription("""
                            Produto: %s
                            Preco: R$%.2f
                            Saldo: R$%.2f
                            """.formatted(storeProduct.getName(), storeProduct.getPrice(), money))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.RED)
                    .setFooter("HotBctsGames");
        }

        Player player = playerRepository.findByIdentity_DiscordId(discordId).orElseThrow();
        Product product = new Product();
        product.setName(storeProduct.getName());
        product.setDescription(storeProduct.getDescription());
        product.setPrice(storeProduct.getPrice());
        product.setStoreProductId(storeProduct.getId());
        product.setPlayer(player);
        productRepository.save(product);

        economy.setMoney(money.subtract(storeProduct.getPrice()));
        economyRepository.save(economy);

        return new EmbedBuilder()
                .setTitle("Compra concluida")
                .setDescription("""
                        Produto: %s
                        Preco: R$%.2f
                        Status: Compra realizada ✅
                        """.formatted(storeProduct.getName(), storeProduct.getPrice()))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.GREEN)
                .setFooter("HotBctsGames");
    }

    private EmbedBuilder buildUsage(MessageReceivedEvent event) {
        return new EmbedBuilder()
                .setTitle("Uso incorreto")
                .setDescription("""
                        Uso correto: .comprar <id>
                        Dica: veja os ids em .loja
                        """)
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.ORANGE)
                .setFooter("HotBctsGames");
    }

    private BigDecimal getMoney(Economy economy) {
        return economy.getMoney() == null ? BigDecimal.ZERO : economy.getMoney();
    }
}
