package top.leonam.hotbctgamess.commands;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.model.entity.Level;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.JobRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;
import top.leonam.hotbctgamess.repository.UniversityRepository;
import top.leonam.hotbctgamess.service.CacheService;

import java.awt.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Random;

public abstract class AbstractTrabalhoCommand implements Command {

    protected JobRepository jobRepository;
    protected EconomyRepository economyRepository;
    protected LevelRepository levelRepository;
    protected UniversityRepository universityRepository;
    protected CacheService cacheService;
    protected GameBalanceProperties.Work workBalance;
    protected Random random;

    protected AbstractTrabalhoCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            UniversityRepository universityRepository,
            CacheService cacheService,
            GameBalanceProperties.Work workBalance,
            Random random
    ) {
        this.jobRepository = jobRepository;
        this.economyRepository = economyRepository;
        this.levelRepository = levelRepository;
        this.universityRepository = universityRepository;
        this.cacheService = cacheService;
        this.workBalance = workBalance;
        this.random = random;
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        Long discordId = event.getAuthor().getIdLong();

        Job job = jobRepository.findByPlayer_Identity_DiscordId(discordId);
        Level level = levelRepository.findByPlayer_Identity_DiscordId(discordId);
        if(level.getLevel() < levelMin()) return buildNotLevel(event, level);

        if (isOnCooldown(job)) return buildCooldownEmbed(event, job);

        BigDecimal ganho = executarTrabalho(job, universityRepository.findByPlayer_Identity_DiscordId(discordId).getConseguiu());
        long total = incrementarEObterTotal(job);
        atualizarEconomia(discordId, ganho);
        atualizarXPLevel(level, false);
        cacheService.evictPlayer(discordId);

        return buildSuccessEmbed(event, ganho, total);
    }

    @Transactional
    protected void atualizarXPLevel(Level level, Boolean foiPreso) {
        if(foiPreso) level.perderXp(minXp());
        else level.ganharXp(minXp());

        levelRepository.save(level);
    }


    protected abstract Long minXp();

    protected EmbedBuilder buildNotLevel(MessageReceivedEvent event, Level level) {
        return new EmbedBuilder()
                .setTitle("Você não tem level suficiente ⭐")
                .setDescription("""
                        Level atual: %d
                        Level minimo: %d
                        """.formatted(level.getLevel(), levelMin()))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.RED)
                .setFooter("HotBctsGames");
    }

    protected abstract int ganhoMin();
    protected abstract int ganhoMax();
    protected abstract int cooldown();
    protected abstract int levelMin();
    protected abstract String descricaoTrabalho();
    protected abstract long incrementarEObterTotal(Job job);

    @Transactional
    protected BigDecimal executarTrabalho(Job job, boolean temFaculdade) {
        job.setLastJob(LocalDateTime.now());
        job.setTotalJobs(job.getTotalJobs() + 1);
        jobRepository.save(job);

        if(!temFaculdade){
            return BigDecimal.valueOf(random.nextInt(ganhoMin(), ganhoMax()));
        }
        BigDecimal base = BigDecimal.valueOf(random.nextInt(ganhoMin(), ganhoMax()));
        return base.multiply(BigDecimal.valueOf(workBalance.getFaculdadeMultiplier()));
    }

    protected void atualizarEconomia(Long discordId, BigDecimal valor) {
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);
        economy.setMoney(economy.getMoney().add(valor));
        economyRepository.save(economy);
    }

    protected boolean isOnCooldown(Job job) {
        if (job.getLastJob() == null) return false;

        LocalDateTime nextAllowed = job.getLastJob().plusSeconds(cooldown());
        return LocalDateTime.now().isBefore(nextAllowed);
    }

    protected long getCooldownSeconds(Job job) {
        LocalDateTime nextAllowed = job.getLastJob().plusSeconds(cooldown());
        return Duration.between(LocalDateTime.now(), nextAllowed).getSeconds();
    }

    protected EmbedBuilder buildCooldownEmbed(MessageReceivedEvent event, Job job) {
        return new EmbedBuilder()
                .setTitle("Trabalhe menos, viva mais.")
                .setDescription("""
                        Status: Cansado 😴
                        Tempo restante: %d segundos
                        """.formatted(getCooldownSeconds(job)))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.RED)
                .setFooter("HotBctsGames");
    }

    protected EmbedBuilder buildSuccessEmbed(
            MessageReceivedEvent event,
            BigDecimal ganho,
            long total
    ) {
        return new EmbedBuilder()
                .setTitle("O trabalho traz dignidade ao homem.")
                .setDescription(descricaoTrabalho().formatted(ganho, total))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.RED)
                .setFooter("HotBctsGames");
    }
}
