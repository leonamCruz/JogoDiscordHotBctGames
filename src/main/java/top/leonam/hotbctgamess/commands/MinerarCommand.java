package top.leonam.hotbctgamess.commands;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Product;
import top.leonam.hotbctgamess.model.entity.Level;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;
import top.leonam.hotbctgamess.repository.ProductRepository;
import top.leonam.hotbctgamess.service.BtcMarketService;
import top.leonam.hotbctgamess.util.MiningCalculator;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;

@Service
public class MinerarCommand implements Command {

    private static final long MIN_ENERGY_PER_ROUND = 1L;
    private static final long MINING_XP = 2L;
    private static final long MINING_LEVEL_MIN = 2L;

    private final EconomyRepository economyRepository;
    private final LevelRepository levelRepository;
    private final ProductRepository productRepository;
    private final BtcMarketService marketService;

    public MinerarCommand(
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            ProductRepository productRepository,
            BtcMarketService marketService
    ) {
        this.economyRepository = economyRepository;
        this.levelRepository = levelRepository;
        this.productRepository = productRepository;
        this.marketService = marketService;
    }

    @Override
    public String name() {
        return ".minerar";
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        Long discordId = event.getAuthor().getIdLong();
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        Level level = levelRepository.findByPlayer_Identity_DiscordId(discordId);
        if (level == null || level.getLevel() < MINING_LEVEL_MIN) {
            return new EmbedBuilder()
                    .setTitle("Level insuficiente")
                    .setDescription("""
                            Level atual: %d
                            Level minimo: %d
                            """.formatted(level == null ? 0L : level.getLevel(), MINING_LEVEL_MIN))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.RED)
                    .setFooter("HotBctsGames");
        }

        LocalDate today = LocalDate.now();
        if (economy.getLastEnergyPayment() == null || !today.equals(economy.getLastEnergyPayment())) {
            return new EmbedBuilder()
                    .setTitle("Energia pendente")
                    .setDescription("""
                            Status: Energia pendente ❌
                            Acao: use .energia para pagar
                            """)
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        List<Product> products = productRepository.findByPlayer_Identity_DiscordId(discordId);
        MiningCalculator.MiningTotals totals = MiningCalculator.calculate(products);
        if (totals.btcPerRound().compareTo(BigDecimal.ZERO) <= 0) {
            return new EmbedBuilder()
                    .setTitle("Sem maquinas")
                    .setDescription("""
                            Status: Nenhuma maquina encontrada
                            Acao: compre uma GPU ou ASIC na .loja
                            """)
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        long energy = getEnergy(economy);
        long energyPerRound = Math.max(MIN_ENERGY_PER_ROUND, totals.energyPerRound());
        if (energy < energyPerRound) {
            return new EmbedBuilder()
                    .setTitle("Sem energia suficiente")
                    .setDescription("""
                            kWh necessario: %d
                            kWh atual: %d
                            """.formatted(energyPerRound, energy))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.RED)
                    .setFooter("HotBctsGames");
        }

        long rounds = energy / energyPerRound;
        BigDecimal btcMined = totals.btcPerRound().multiply(BigDecimal.valueOf(rounds));

        economy.setEnergy(energy - (rounds * energyPerRound));
        economy.setBtc(getBtc(economy).add(btcMined));
        economyRepository.save(economy);
        marketService.applyMining(btcMined);
        addMiningXp(discordId);

        return new EmbedBuilder()
                .setTitle("Mineracao automatica concluida")
                .setDescription("""
                        BTC minerado: %.5f 🪙
                        kWh restante: %d ⚡
                        Status: Luz cortada
                        """.formatted(btcMined, economy.getEnergy()))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.GREEN)
                .setFooter("HotBctsGames");
    }

    private long getEnergy(Economy economy) {
        return economy.getEnergy() == null ? 0L : economy.getEnergy();
    }

    private BigDecimal getBtc(Economy economy) {
        return economy.getBtc() == null ? BigDecimal.ZERO : economy.getBtc();
    }

    private void addMiningXp(Long discordId) {
        Level level = levelRepository.findByPlayer_Identity_DiscordId(discordId);
        if (level == null) {
            return;
        }
        level.ganharXp(MINING_XP);
        levelRepository.save(level);
    }
}
