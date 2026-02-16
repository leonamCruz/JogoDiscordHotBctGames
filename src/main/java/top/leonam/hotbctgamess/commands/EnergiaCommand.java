package top.leonam.hotbctgamess.commands;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.service.CacheService;
import top.leonam.hotbctgamess.service.EnergyService;

import java.awt.*;
import java.time.Instant;

@Service
public class EnergiaCommand implements Command {

    private final EconomyRepository economyRepository;
    private final EnergyService energyService;
    private final CacheService cacheService;

    public EnergiaCommand(
            EconomyRepository economyRepository,
            EnergyService energyService,
            CacheService cacheService
    ) {
        this.economyRepository = economyRepository;
        this.energyService = energyService;
        this.cacheService = cacheService;
    }

    @Override
    public String name() {
        return ".energia";
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        Long discordId = event.getAuthor().getIdLong();
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);

        String[] parts = event.getMessage().getContentRaw().trim().split("\\s+");
        int extraPacks = parseExtraPacks(parts);

        EnergyService.EnergyStatus status = energyService.getStatus(discordId);
        boolean alreadyPaidToday = status.alreadyPaid();
        if (alreadyPaidToday && extraPacks == 0) {
            return new EmbedBuilder()
                    .setTitle("Energia ja paga")
                    .setDescription("""
                            Status: Energia diaria paga ✅
                            kWh atual: %d ⚡
                            Dica: use .energia <packs> para comprar energia extra (+%d kWh por pack)
                            """.formatted(getEnergy(economy), energyService.getExtraPackSize()))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        EnergyService.EnergyPurchaseResult result = energyService.purchaseEnergy(discordId, extraPacks, true);
        if (!result.success()) {
            return new EmbedBuilder()
                    .setTitle("Dinheiro insuficiente")
                    .setDescription("""
                            Valor necessario: R$%.2f
                            Saldo atual: R$%.2f
                            """.formatted(result.totalCost(), economy.getMoney()))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.RED)
                    .setFooter("HotBctsGames");
        }

        cacheService.evictPlayer(discordId);

        return new EmbedBuilder()
                .setTitle("Energia carregada")
                .setDescription("""
                        Valor pago: R$%.2f
                        kWh adicionados: +%d
                        kWh atual: %d ⚡
                        """.formatted(result.totalCost(), result.energyAdded(), result.currentEnergy()))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.GREEN)
                .setFooter("HotBctsGames");
    }

    private long getEnergy(Economy economy) {
        return economy.getEnergy() == null ? 0L : economy.getEnergy();
    }

    private int parseExtraPacks(String[] parts) {
        if (parts.length < 2) {
            return 0;
        }
        try {
            int value = Integer.parseInt(parts[1]);
            return Math.max(0, value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
