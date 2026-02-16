package top.leonam.hotbctgamess.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.model.entity.Level;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.JobRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;
import top.leonam.hotbctgamess.repository.PlayerRepository;
import top.leonam.hotbctgamess.repository.ProductRepository;
import top.leonam.hotbctgamess.util.RankingCalculator;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Optional;

@Service
public class PerfilCommand implements Command {

    private final EconomyRepository economyRepository;
    private final JobRepository jobRepository;
    private final LevelRepository levelRepository;
    private final PlayerRepository playerRepository;
    private final ProductRepository productRepository;

    public PerfilCommand(
            EconomyRepository economyRepository,
            JobRepository jobRepository,
            LevelRepository levelRepository,
            PlayerRepository playerRepository,
            ProductRepository productRepository
    ) {
        this.economyRepository = economyRepository;
        this.jobRepository = jobRepository;
        this.levelRepository = levelRepository;
        this.playerRepository = playerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public String name() {
        return ".perfil";
    }

    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        Member targetMember = event.getMember();
        if (!event.getMessage().getMentions().getMembers().isEmpty()) {
            targetMember = event.getMessage().getMentions().getMembers().getFirst();
        }

        if (targetMember == null) {
            return new EmbedBuilder()
                    .setTitle("Perfil nao encontrado 👤")
                    .setDescription("Nao foi possivel identificar o usuario.")
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        Long discordId = targetMember.getIdLong();
        Optional<Player> player = playerRepository.findByIdentity_DiscordId(discordId);
        if (player.isEmpty()) {
            return new EmbedBuilder()
                    .setTitle("Perfil vazio 👤")
                    .setDescription("Esse jogador ainda nao iniciou.")
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        Job job = jobRepository.findByPlayer_Identity_DiscordId(discordId);
        Level level = levelRepository.findByPlayer_Identity_DiscordId(discordId);
        long products = productRepository.countByPlayer_Identity_DiscordId(discordId);

        BigDecimal money = economy == null ? BigDecimal.ZERO : safeMoney(economy.getMoney());
        BigDecimal btc = economy == null ? BigDecimal.ZERO : safeMoney(economy.getBtc());
        long totalJobs = job == null ? 0L : safeLong(job.getTotalJobs());
        long totalCrimes = job == null ? 0L : safeLong(job.getTotalCrimes());
        long currentLevel = level == null ? 1L : level.getLevel();
        double score = RankingCalculator.score(money, btc, totalJobs, totalCrimes, products);
        long totalIfood = job == null ? 0L : safeLong(job.getTotalIfood());
        long totalUber = job == null ? 0L : safeLong(job.getTotalUber());
        long totalEstoque = job == null ? 0L : safeLong(job.getTotalEstoque());
        long totalGarcom = job == null ? 0L : safeLong(job.getTotalGarcom());
        long totalPedreiro = job == null ? 0L : safeLong(job.getTotalPedreiro());
        long totalCc = job == null ? 0L : safeLong(job.getTotalCc());
        long totalTrafico = job == null ? 0L : safeLong(job.getTotalTrafico());
        long totalSequestro = job == null ? 0L : safeLong(job.getTotalSequestro());
        long totalHackear = job == null ? 0L : safeLong(job.getTotalHackear());
        long totalLaranja = job == null ? 0L : safeLong(job.getTotalLaranja());
        long totalBet = job == null ? 0L : safeLong(job.getTotalBet());
        long totalRoubar = job == null ? 0L : safeLong(job.getTotalRoubar());

        String descricao = """
                Score: %.1f ⭐
                Level: %d 🎖️
                Dinheiro: R$%.2f 💰
                BTC: %s 🪙
                Trabalhos: %d 🛠
                Crimes: %d 😈
                Produtos: %d 📦
                iFood: %d
                Uber: %d
                Estoque: %d
                Garcom: %d
                Pedreiro: %d
                CC: %d
                Trafico: %d
                Sequestro: %d
                Hackear: %d
                Laranja: %d
                Bet: %d
                Roubar: %d
                """.formatted(
                score,
                currentLevel,
                money,
                formatBtc(btc),
                totalJobs,
                totalCrimes,
                products,
                totalIfood,
                totalUber,
                totalEstoque,
                totalGarcom,
                totalPedreiro,
                totalCc,
                totalTrafico,
                totalSequestro,
                totalHackear,
                totalLaranja,
                totalBet,
                totalRoubar
        );

        return new EmbedBuilder()
                .setTitle("Perfil de %s 👤".formatted(targetMember.getEffectiveName()))
                .setDescription(descricao)
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(targetMember.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.CYAN)
                .setFooter("HotBctsGames");
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
}
