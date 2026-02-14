package top.leonam.hotbctgamess.commands;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public abstract class AbstractCrimeCommand extends AbstractTrabalhoCommand {
    protected PrisonRepository prisonRepository;

    protected AbstractCrimeCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            PrisonRepository prisonRepository,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, random);
        this.prisonRepository = prisonRepository;
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        Long discordId = event.getAuthor().getIdLong();

        Prison prison = prisonRepository.findByPlayer_Identity_DiscordId(discordId);

        // Checa se está preso e se já passou o tempo da prisão
        if (prison != null && prison.getLastPrison() != null) {
            long segundosRestantes = segundosRestantesPrisao(prison);
            if (segundosRestantes > 0) {
                return buildPresoEmbed(event, segundosRestantes);
            } else {
                // tempo acabou, libera o jogador
                prison.setLastPrison(null);
                prisonRepository.save(prison);
            }
        }

        Job job = jobRepository.findByPlayer_Identity_DiscordId(discordId);
        Level level = levelRepository.findByPlayer_Identity_DiscordId(discordId);

        if (level.getLevel() < levelMin()) return buildNotLevel(event, level);

        if (isOnCooldown(job)) return buildCooldownEmbed(event, job);

        // Atualiza trabalho
        job.setLastJob(LocalDateTime.now());
        job.setTotalDeliveries(job.getTotalDeliveries() + 1);
        jobRepository.save(job);

        // Checa se foi preso
        if (foiPreso()) {
            aplicarPenalidade(discordId);
            atualizarXPLevel(level, true);
            prison = prisonRepository.findByPlayer_Identity_DiscordId(discordId);
            return buildPresoEmbed(event, segundosRestantesPrisao(prison));
        }

        BigDecimal ganho = BigDecimal.valueOf(random.nextInt(ganhoMin(), ganhoMax()));
        atualizarEconomia(discordId, ganho);
        atualizarXPLevel(level, false);

        return buildSuccessEmbed(event, ganho, job.getTotalDeliveries());
    }

    // calcula quantos segundos faltam de prisão
    protected long segundosRestantesPrisao(Prison prison) {
        if (prison == null || prison.getLastPrison() == null) return 0;

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
            prison.setPlayer(levelRepository.findByPlayer_Identity_DiscordId(discordId).getPlayer());
        }
        prison.setLastPrison(LocalDateTime.now());
        prisonRepository.save(prison);
    }

    protected BigDecimal multaPrisao() {
        return BigDecimal.valueOf(random.nextInt(ganhoMin(), ganhoMax()));
    }

    protected abstract int chancePrisao();
    protected abstract String textoPrisao();

    protected EmbedBuilder buildPresoEmbed(MessageReceivedEvent event, long segundosRestantes) {
        return new EmbedBuilder()
                .setTitle("Você está preso 🚓")
                .setDescription("""
                        Faltam %d segundos pra você sair.
                        Ou então pague a fiança ⚖️
                        """.formatted(segundosRestantes))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setTimestamp(LocalDateTime.now())
                .setColor(Color.DARK_GRAY)
                .setThumbnail("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExcjZydGFlejQ0a2ZiOXM4eHRjYm83OGRvdzZxcDF3czk5aTI2MXR4YSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/kyQ5ow8qDy78VccGyg/giphy.gif")
                .setFooter("HotBctsGames - Dica: pague a fiança com ~fiança");
    }
}
