package top.leonam.hotbctgamess.commands;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.model.entity.Level;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.JobRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;
import top.leonam.hotbctgamess.repository.PrisonRepository;

import java.awt.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@RequiredArgsConstructor
public abstract class AbstractTrabalhoCommand implements Command {

    protected final JobRepository jobRepository;
    protected final EconomyRepository economyRepository;
    protected final LevelRepository levelRepository;
    protected final Random random;

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        Long discordId = event.getAuthor().getIdLong();

        Job job = jobRepository.findByPlayer_Identity_DiscordId(discordId);
        Level level = levelRepository.findByPlayer_Identity_DiscordId(discordId);
        if(level.getLevel() < levelMin()) return buildNotLevel(event, level);

        if (isOnCooldown(job)) return buildCooldownEmbed(event, job);

        BigDecimal ganho = executarTrabalho(job);
        atualizarEconomia(discordId, ganho);
        atualizarXPLevel(level, false);

        return buildSuccessEmbed(event, ganho, job.getTotalDeliveries());
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
                        Atualmente você é level %d e precisa de no minimo %d.
                        """.formatted(level.getLevel(), levelMin()))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.RED)
                .setFooter("HotBctsGames");
    }

    protected abstract int ganhoMin();
    protected abstract int ganhoMax();
    protected abstract int cooldown();
    protected abstract int levelMin();
    protected abstract String descricaoTrabalho();

    @Transactional
    protected BigDecimal executarTrabalho(Job job) {
        job.setLastJob(LocalDateTime.now());
        job.setTotalDeliveries(job.getTotalDeliveries() + 1);
        jobRepository.save(job);

        return BigDecimal.valueOf(random.nextInt(ganhoMin(), ganhoMax()));
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
                        Você está cansado. Espere %d segundos.
                        """.formatted(getCooldownSeconds(job)))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setTimestamp(LocalDateTime.now())
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
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.RED)
                .setFooter("HotBctsGames");
    }
}
