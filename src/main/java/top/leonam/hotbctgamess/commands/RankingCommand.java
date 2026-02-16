package top.leonam.hotbctgamess.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.dto.RankingRow;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.PlayerRepository;
import top.leonam.hotbctgamess.repository.ProductRepository;
import top.leonam.hotbctgamess.util.RankingCalculator;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RankingCommand implements Command {

    private final PlayerRepository playerRepository;

    public RankingCommand(
            PlayerRepository playerRepository
    ) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String name() {
        return ".ranking";
    }

    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        List<RankingRow> rows = playerRepository.findRankingRows();
        if (rows.isEmpty()) {
            return new EmbedBuilder()
                    .setTitle("Ranking vazio 🏆")
                    .setDescription("Ainda nao ha jogadores no ranking.")
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        List<RankingEntry> entries = new ArrayList<>();
        for (RankingRow row : rows) {
            BigDecimal money = safeMoney(row.money());
            BigDecimal btc = safeMoney(row.btc());
            long totalJobs = safeLong(row.totalJobs());
            long totalCrimes = safeLong(row.totalCrimes());
            long currentLevel = row.level() == null ? 1L : row.level();
            long products = row.products() == null ? 0L : row.products();

            double score = RankingCalculator.score(money, btc, totalJobs, totalCrimes, products);
            entries.add(new RankingEntry(
                    row.name(),
                    row.discordId(),
                    score,
                    money,
                    btc,
                    totalJobs,
                    totalCrimes,
                    products,
                    currentLevel
            ));
        }

        entries.sort(Comparator.comparingDouble(RankingEntry::score).reversed());

        StringBuilder list = new StringBuilder();
        int position = 1;
        for (RankingEntry entry : entries.stream().limit(10).toList()) {
            list.append("""
                    %d. %s
                    Score: %.1f ⭐
                    Level: %d 🎖️
                    Dinheiro: R$%.2f 💰
                    BTC: %s 🪙
                    Trabalhos: %d 🛠
                    Crimes: %d 😈
                    Itens: %d 📦
                    """.formatted(
                            position++,
                            entry.name,
                            entry.score,
                            entry.level,
                            entry.money,
                            formatBtc(entry.btc),
                            entry.totalJobs,
                            entry.totalCrimes,
                            entry.products
                    )).append("\n");
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Ranking Geral 🏆")
                .setDescription(list.toString())
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.YELLOW)
                .setFooter("HotBctsGames");
        if (!entries.isEmpty()) {
            String avatarUrl = resolveAvatarUrl(event, entries.getFirst().discordId);
            if (avatarUrl != null) {
                embed.setThumbnail(avatarUrl);
            }
        }
        return embed;
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }

    private String formatBtc(BigDecimal value) {
        return value.setScale(8, RoundingMode.HALF_UP).toPlainString();
    }

    private record RankingEntry(
            String name,
            long discordId,
            double score,
            BigDecimal money,
            BigDecimal btc,
            long totalJobs,
            long totalCrimes,
            long products,
            long level
    ) {
    }

    private String resolveAvatarUrl(MessageReceivedEvent event, long discordId) {
        var cached = event.getJDA().getUserById(discordId);
        if (cached != null) {
            return cached.getEffectiveAvatarUrl();
        }
        try {
            var user = event.getJDA().retrieveUserById(discordId).complete();
            return user == null ? null : user.getEffectiveAvatarUrl();
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
