package top.leonam.hotbctgamess.commands;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.model.entity.Level;
import top.leonam.hotbctgamess.model.entity.Prison;
import top.leonam.hotbctgamess.repository.*;
import top.leonam.hotbctgamess.service.CacheService;

import java.awt.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Random;

public abstract class AbstractCrimeCommand extends AbstractTrabalhoCommand {

    protected final PrisonRepository prisonRepository;

    protected AbstractCrimeCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            PrisonRepository prisonRepository,
            UniversityRepository universityRepository,
            CacheService cacheService,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, universityRepository, cacheService, random);
        this.prisonRepository = prisonRepository;
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {

        Long discordId = event.getAuthor().getIdLong();

        Prison prison = prisonRepository.findByPlayer_Identity_DiscordId(discordId);

        // 1️⃣ Checa prisão
        if (prison != null && prison.getLastPrison() != null) {
            long restantes = segundosRestantesPrisao(prison);
            if (restantes > 0) {
                return buildPresoEmbed(event, restantes);
            }
            prison.setLastPrison(null);
            prisonRepository.save(prison);
        }

        Job job = jobRepository.findByPlayer_Identity_DiscordId(discordId);
        Level level = levelRepository.findByPlayer_Identity_DiscordId(discordId);

        // 2️⃣ Checa nível
        if (level.getLevel() < levelMin()) return buildNotLevel(event, level);

        // 3️⃣ Cooldown de CRIME
        if (isCrimeOnCooldown(job)) return buildCrimeCooldownEmbed(event, job);

        // 4️⃣ Atualiza crime
        job.setLastCrime(LocalDateTime.now());
        job.setTotalCrimes(job.getTotalCrimes() + 1);
        jobRepository.save(job);

        // 5️⃣ Prisão
        if (foiPreso()) {
            aplicarPenalidade(discordId);
            atualizarXPLevel(level, true);
            cacheService.evictPlayer(discordId);

            prison = prisonRepository.findByPlayer_Identity_DiscordId(discordId);
            return buildPresoEmbed(event, segundosRestantesPrisao(prison));
        }

        // 6️⃣ Sucesso
        BigDecimal ganho = BigDecimal.valueOf(random.nextInt(ganhoMin(), ganhoMax()));
        long total = incrementarEObterTotal(job);
        atualizarEconomia(discordId, ganho);
        atualizarXPLevel(level, false);
        cacheService.evictPlayer(discordId);

        return buildSuccessEmbed(event, ganho, total);
    }

    // ================= COOLDOWN CRIME =================

    protected boolean isCrimeOnCooldown(Job job) {
        if (job.getLastCrime() == null) return false;

        long segundos = Duration
                .between(job.getLastCrime(), LocalDateTime.now())
                .getSeconds();

        return segundos < cooldown();
    }

    protected EmbedBuilder buildCrimeCooldownEmbed(MessageReceivedEvent event, Job job) {
        long restantes = cooldown() - Duration
                .between(job.getLastCrime(), LocalDateTime.now())
                .getSeconds();

        return new EmbedBuilder()
                .setTitle("Aguarde ⏳")
                .setDescription("""
                        Tempo restante: %d segundos
                        Status: Crime em cooldown
                        """.formatted(restantes))
                .setColor(Color.ORANGE)
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
    }

    // ================= PRISÃO =================

    protected long segundosRestantesPrisao(Prison prison) {
        LocalDateTime fim = prison.getLastPrison().plusSeconds(cooldown());
        long segundos = Duration.between(LocalDateTime.now(), fim).getSeconds();
        return Math.max(segundos, 0);
    }

    protected boolean foiPreso() {
        return random.nextInt(100) < chancePrisao();
    }

    @Transactional
    protected void aplicarPenalidade(Long discordId) {
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        economy.setMoney(economy.getMoney().subtract(multaPrisao()));
        economyRepository.save(economy);

        Prison prison = prisonRepository.findByPlayer_Identity_DiscordId(discordId);
        if (prison == null) {
            prison = new Prison();
            prison.setPlayer(levelRepository
                    .findByPlayer_Identity_DiscordId(discordId)
                    .getPlayer());
        }

        prison.setLastPrison(LocalDateTime.now());
        prisonRepository.save(prison);
    }

    protected BigDecimal multaPrisao() {
        return BigDecimal.valueOf(random.nextInt(ganhoMin(), ganhoMax()));
    }

    protected abstract int chancePrisao();
    protected abstract String textoPrisao();
    protected abstract long incrementarEObterTotal(Job job);

    // 🔥 GIF MANTIDO
    protected EmbedBuilder buildPresoEmbed(MessageReceivedEvent event, long segundosRestantes) {
        return new EmbedBuilder()
                .setTitle("Você está preso 🚓")
                .setDescription(""" 
                        Motivo: %s
                        Tempo restante: %d segundos
                        Dica: pague a fianca ⚖️
                        """.formatted(textoPrisao(), segundosRestantes))
                .setAuthor(event.getMember().getEffectiveName())
                .setTimestamp(Instant.now())
                .setColor(Color.DARK_GRAY)
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setImage("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExcjZydGFlejQ0a2ZiOXM4eHRjYm83OGRvdzZxcDF3czk5aTI2MXR4YSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/kyQ5ow8qDy78VccGyg/giphy.gif")
                .setFooter("HotBctsGames - Dica: pague a fiança com .fiança");
    }
}
