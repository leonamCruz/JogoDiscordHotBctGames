package top.leonam.hotbctgamess.commands;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.model.entity.Level;
import top.leonam.hotbctgamess.model.entity.Prison;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.JobRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;
import top.leonam.hotbctgamess.repository.PrisonRepository;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;

@Service
public class RoubarCommand implements Command {

    private static final int DAILY_LIMIT = 3;
    private static final int PRISON_CHANCE = 15;
    private static final long XP_PENALTY = 15L;

    private final JobRepository jobRepository;
    private final EconomyRepository economyRepository;
    private final LevelRepository levelRepository;
    private final PrisonRepository prisonRepository;
    private final Random random;

    public RoubarCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            PrisonRepository prisonRepository,
            Random random
    ) {
        this.jobRepository = jobRepository;
        this.economyRepository = economyRepository;
        this.levelRepository = levelRepository;
        this.prisonRepository = prisonRepository;
        this.random = random;
    }

    @Override
    public String name() {
        return ".roubar";
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        if (event.getMessage().getMentions().getMembers().isEmpty()) {
            return new EmbedBuilder()
                    .setTitle("Roubo invalido 🧤")
                    .setDescription("""
                            Acao: mencione alguem para roubar
                            Exemplo: .roubar @user
                            """)
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        Member alvo = event.getMessage().getMentions().getMembers().getFirst();
        if (alvo.getIdLong() == event.getAuthor().getIdLong()) {
            return new EmbedBuilder()
                    .setTitle("Roubo invalido 🚫")
                    .setDescription("""
                            Status: voce nao pode se roubar
                            Dica: mencione outro jogador
                            """)
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.RED)
                    .setFooter("HotBctsGames");
        }

        Long discordId = event.getAuthor().getIdLong();
        Job job = jobRepository.findByPlayer_Identity_DiscordId(discordId);

        LocalDate today = LocalDate.now();
        if (job.getLastRobberyDate() == null || !today.equals(job.getLastRobberyDate())) {
            job.setLastRobberyDate(today);
            job.setRobberiesToday(0L);
        }
        if (job.getRobberiesToday() == null) {
            job.setRobberiesToday(0L);
        }

        if (job.getRobberiesToday() >= DAILY_LIMIT) {
            return new EmbedBuilder()
                    .setTitle("Limite diario atingido ⏳")
                    .setDescription("""
                            Limite: %d roubos por dia
                            Status: volte amanha
                            """.formatted(DAILY_LIMIT))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.ORANGE)
                    .setFooter("HotBctsGames");
        }

        job.setRobberiesToday(job.getRobberiesToday() + 1);
        jobRepository.save(job);

        if (random.nextInt(100) < PRISON_CHANCE) {
            return aplicarPrisao(event, discordId);
        }

        Economy attackerEconomy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        Economy targetEconomy = economyRepository.findByPlayer_Identity_DiscordId(alvo.getIdLong());

        if (targetEconomy == null || safeMoney(targetEconomy.getMoney()).compareTo(BigDecimal.ZERO) <= 0) {
            return new EmbedBuilder()
                    .setTitle("Roubo falhou 😶")
                    .setDescription("""
                            Alvo: %s
                            Motivo: alvo sem dinheiro
                            """.formatted(alvo.getEffectiveName()))
                    .setAuthor(event.getAuthor().getEffectiveName())
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.GRAY)
                    .setFooter("HotBctsGames");
        }

        BigDecimal targetMoney = safeMoney(targetEconomy.getMoney());
        int percent = 5 + random.nextInt(16);
        BigDecimal stolen = targetMoney.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        targetEconomy.setMoney(targetMoney.subtract(stolen));
        attackerEconomy.setMoney(safeMoney(attackerEconomy.getMoney()).add(stolen));
        economyRepository.save(targetEconomy);
        economyRepository.save(attackerEconomy);

        return new EmbedBuilder()
                .setTitle("Roubo concluido 🤑")
                .setDescription("""
                        Alvo: %s
                        Percentual: %d%%
                        Valor roubado: R$%.2f 💸
                        Roubos hoje: %d/%d
                        """.formatted(
                        alvo.getEffectiveName(),
                        percent,
                        stolen,
                        job.getRobberiesToday(),
                        DAILY_LIMIT
                ))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.GREEN)
                .setFooter("HotBctsGames");
    }

    private EmbedBuilder aplicarPrisao(MessageReceivedEvent event, Long discordId) {
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        Level level = levelRepository.findByPlayer_Identity_DiscordId(discordId);

        BigDecimal money = safeMoney(economy.getMoney());
        BigDecimal loss = money.multiply(new BigDecimal("0.50"));
        economy.setMoney(money.subtract(loss));
        economyRepository.save(economy);

        if (level != null) {
            level.perderXp(XP_PENALTY);
            levelRepository.save(level);
        }

        Prison prison = prisonRepository.findByPlayer_Identity_DiscordId(discordId);
        if (prison == null) {
            prison = new Prison();
            prison.setPlayer(levelRepository
                    .findByPlayer_Identity_DiscordId(discordId)
                    .getPlayer());
        }
        prison.setLastPrison(java.time.LocalDateTime.now());
        prisonRepository.save(prison);

        return new EmbedBuilder()
                .setTitle("Você está preso 🚓")
                .setDescription("""
                        Motivo: Roubo frustrado 🚨
                        Penalidade: -%d XP
                        Multa: R$%.2f
                        Dica: pague a fiança
                        """.formatted(XP_PENALTY, loss))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.DARK_GRAY)
                .setFooter("HotBctsGames");
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
