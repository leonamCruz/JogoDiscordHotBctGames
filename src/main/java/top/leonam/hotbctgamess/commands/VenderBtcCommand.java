package top.leonam.hotbctgamess.commands;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Level;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;
import top.leonam.hotbctgamess.service.CacheService;
import top.leonam.hotbctgamess.service.BtcMarketService;
import top.leonam.hotbctgamess.service.TaxService;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;

@Service
public class VenderBtcCommand implements Command {

    private final EconomyRepository economyRepository;
    private final LevelRepository levelRepository;
    private final BtcMarketService marketService;
    private final TaxService taxService;
    private final CacheService cacheService;
    private final GameBalanceProperties.Btc balance;

    public VenderBtcCommand(
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            BtcMarketService marketService,
            TaxService taxService,
            CacheService cacheService,
            GameBalanceProperties balanceProperties
    ) {
        this.economyRepository = economyRepository;
        this.levelRepository = levelRepository;
        this.marketService = marketService;
        this.taxService = taxService;
        this.cacheService = cacheService;
        this.balance = balanceProperties.getBtc();
    }

    @Override
    public String name() {
        return ".venderbtc";
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        String[] parts = event.getMessage().getContentRaw().trim().split("\\s+");
        if (parts.length < 2) {
            return buildUsage(event);
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(parts[1]);
        } catch (NumberFormatException ex) {
            return buildUsage(event);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return buildUsage(event);
        }

        Long discordId = event.getAuthor().getIdLong();
        Level level = levelRepository.findByPlayer_Identity_DiscordId(discordId);
        if (level == null || level.getLevel() < balance.getSellLevelMin()) {
            return new EmbedBuilder()
                    .setTitle("Level insuficiente")
                    .setDescription("""
                            Level atual: %d
                            Level minimo: %d
                            """.formatted(level == null ? 0L : level.getLevel(), balance.getSellLevelMin()))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.RED)
                    .setFooter("HotBctsGames");
        }
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        BigDecimal btc = economy.getBtc() == null ? BigDecimal.ZERO : economy.getBtc();

        if (btc.compareTo(amount) < 0) {
            return new EmbedBuilder()
                    .setTitle("BTC insuficiente")
                    .setDescription("""
                            BTC solicitado: %.5f
                            BTC disponivel: %.5f
                            """.formatted(amount, btc))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.RED)
                    .setFooter("HotBctsGames");
        }

        BigDecimal price = marketService.getCurrentPrice();
        BigDecimal gross = amount.multiply(price);

        if (taxService.isTaxable(gross)) {
            BigDecimal tax = taxService.calculateTax(gross);
            BigDecimal total = gross.subtract(tax);
            return new EmbedBuilder()
                    .setTitle("Imposto necessario")
                    .setDescription("""
                            Preco BTC: R$%.2f
                            BTC a vender: %.5f
                            Valor bruto: R$%.2f
                            Imposto (%s%%): R$%.2f
                            Valor liquido: R$%.2f
                            Escolha: pagar ou sonegar
                            """.formatted(price, amount, gross, formatPercent(taxService.getRate()), tax, total))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        economy.setBtc(btc.subtract(amount));
        economy.setMoney(getMoney(economy).add(gross));
        economyRepository.save(economy);
        marketService.applySell(amount);
        cacheService.evictPlayer(discordId);

        return new EmbedBuilder()
                .setTitle("Venda concluida")
                .setDescription("""
                        Preco BTC: R$%.2f
                        BTC vendido: %.5f
                        Valor recebido: R$%.2f
                        """.formatted(price, amount, gross))
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
                        Use: .venderbtc <quantidade>
                        Exemplo: .venderbtc 0.005
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

    private String formatPercent(BigDecimal rate) {
        return rate.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString();
    }
}
