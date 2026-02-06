package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.*;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.model.enums.TypeTransaction;
import top.leonam.hotbctgamess.repository.PrisonRepository;

import java.awt.Color; // Importante para as cores
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class PrisonService {

    private final PlayerService playerService;
    private final PrisonRepository prisonRepository;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CrimeHistoryService crimeHistoryService;

    public Prison getPrisonByDiscordId(Long idDiscord) {
        return prisonRepository
                .findByPlayer_Identity_DiscordId(idDiscord)
                .orElseThrow();
    }

    public void save(Prison prison) {
        prisonRepository.save(prison);
    }

    @Transactional
    public EmbedBuilder payBail(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();

        Long idDiscord = event.getAuthor().getIdLong();
        Long stateId = event.getJDA().getSelfUser().getIdLong();

        Prison prison = getPrisonByDiscordId(idDiscord);

        if (prison.getStatus() == PrisonStatus.SOLTO) {
            embed.setColor(Color.GREEN);
            embed.setTitle("🟢 Você já está solto");
            embed.setDescription("O Estado agradece a tentativa de doação, mas você é um cidadão livre.");
            return embed;
        }

        CrimeHistory lastCrime = crimeHistoryService.getLastCrime(idDiscord);

        if (lastCrime == null) {
            embed.setColor(Color.ORANGE);
            embed.setTitle("❓ Erro de Registro");
            embed.setDescription("Nenhum crime registrado. Algo aqui cheira a bug na matrix.");
            return embed;
        }

        Player player = lastCrime.getPlayer();
        BigDecimal bailValue = calculateBail(lastCrime.getCrime(), player);

        Account accountFrom = accountService.getAccountByDiscordId(idDiscord);
        Account accountTo = accountService.getAccountByDiscordId(stateId);

        boolean paid = transactionService.transfer(
                accountFrom,
                accountTo,
                bailValue,
                TypeTransaction.FIANCA
        );

        if (!paid) {
            embed.setColor(Color.RED);
            embed.setTitle("🚫 Fiança Recusada");
            embed.setDescription("Seu saldo é insuficiente para comprar sua liberdade.");

            embed.addField("💰 Valor Exigido", String.format("R$ %.2f", bailValue), true);
            embed.addField("📉 Motivo", "Saldo Insuficiente", true);

            embed.setFooter("Continue refletindo atrás das grades.", event.getAuthor().getEffectiveAvatarUrl());
            return embed;
        }

        playerService.addXp(
                player,
                -Math.min(player.getCurrentXp(), lastCrime.getCrime().getXp())
        );

        prison.setStatus(PrisonStatus.SOLTO);
        prison.setReleaseAt(LocalDateTime.now());
        prison.setJailedAt(null);

        prisonRepository.save(prison);

        embed.setTitle("🏛️ Fiança Paga com Sucesso");
        embed.setDescription("A justiça foi... estimulada financeiramente. Você está livre.");

        embed.addField("💸 Valor Pago", String.format("R$ %.2f", bailValue), true);
        embed.addField("📉 XP Perdido", String.format("-%d XP", lastCrime.getCrime().getXp()), true);
        embed.addField("🔓 Novo Status", "Livre", false); // false para ocupar a linha inteira se quiser

        embed.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());
        embed.setFooter("Tente não voltar tão cedo.");

        return embed;
    }

    @Transactional
    public boolean checkAndRelease(Player player) {
        Prison prison = player.getPrison();

        if (prison.getStatus() == PrisonStatus.PRESO &&
                prison.getReleaseAt().isBefore(LocalDateTime.now())) {

            prison.setStatus(PrisonStatus.SOLTO);
            prisonRepository.save(prison);
            return true;
        }
        return false;
    }

    public BigDecimal calculateBail(Crime crime, Player player) {

        BigDecimal rewardPart =
                crime.getMaxReward().multiply(BigDecimal.valueOf(0.3));

        BigDecimal cooldownPart =
                BigDecimal.valueOf(crime.getCooldownSeconds() * 10L);

        BigDecimal levelMultiplier =
                BigDecimal.valueOf(1 + player.getCurrentLevel() * 0.05);

        return rewardPart
                .add(cooldownPart)
                .multiply(levelMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }
}