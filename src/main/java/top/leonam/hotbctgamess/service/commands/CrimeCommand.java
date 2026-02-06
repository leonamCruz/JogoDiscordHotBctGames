package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.exceptions.UserNotFound;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.*;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.model.enums.TypeTransaction;
import top.leonam.hotbctgamess.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
@Service
@AllArgsConstructor
@Slf4j
public class CrimeCommand implements Command {

    private final CrimeService crimeService;
    private final PlayerService playerService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CrimeHistoryService crimeHistoryService;
    private final PrisonService prisonService;
    private final PrisonHistoryService prisonHistoryService;

    @Override
    public String name() {
        return "?cometer";
    }

    @Override
    @Transactional
    public String execute(MessageReceivedEvent event) {
        long playerId = event.getAuthor().getIdLong();
        Player player;

        try {
            player = playerService.getPlayer(playerId);
        } catch (UserNotFound e) {
            return "👤 Você ainda não está registrado no jogo.";
        }
        prisonService.checkAndRelease(player);

        if (player.getPrison().getStatus() == PrisonStatus.PRESO) {
            return "🔒 Você ainda está preso. Aguarde o tempo acabar ou pague a fiança.";
        }
        String[] parts = event.getMessage().getContentRaw().split("\\s+");

        if (parts.length != 2) {
            return "❓ Uso correto: `?cometer <crime>`";
        }

        String crimeName = parts[1].toLowerCase();
        Crime crime;

        try {
            crime = crimeService.getCrimeByName(crimeName);
        } catch (NoSuchElementException e) {
            return "🚫 Esse crime **não existe**.";
        }

        if (player.getCurrentLevel() < crime.getMinLevel()) {
            return """
                    🚫 Nível insuficiente
                    
                    🔓 Necessário: nível %d
                    📉 Seu nível: %d
                    """.formatted(crime.getMinLevel(), player.getCurrentLevel());
        }

        boolean success = CrimeUtils.successCrime(crime);
        boolean arrested = !success && CrimeUtils.policeArrested(crime);

        BigDecimal reward = BigDecimal.valueOf(CrimeUtils.randomValueCrime(crime));
        LocalDateTime now = LocalDateTime.now();

        CrimeHistory crimeHistory = CrimeHistory.builder()
                .player(player)
                .crime(crime)
                .attemptedAt(now)
                .success(success)
                .reward(success ? reward : BigDecimal.ZERO)
                .jailed(arrested)
                .jailTimeSeconds(arrested ? crime.getCooldownSeconds() : 0)
                .build();

        crimeHistoryService.save(crimeHistory);

        Account account = accountService.getAccountByDiscordId(playerId);

        if (success && !arrested) {
            transactionService.deposit(account, reward, TypeTransaction.CRIME);
            playerService.addXp(player, crime.getXp());

            return """
                    🔪 **Crime bem-sucedido**
                    
                    🧾 Crime: %s
                    💰 Lucro: R$ %.2f
                    ⭐ XP ganho: %d
                    """.formatted(
                    crime.getName(),
                    reward.doubleValue(),
                    crime.getXp()
            );
        }

        // ❌ falhou
        playerService.addXp(player, -crime.getXp());

        if (arrested) {
            Prison prison = prisonService.getPrisonByDiscordId(playerId);

            prison.setStatus(PrisonStatus.PRESO);
            prison.setJailedAt(now);
            prison.setReleaseAt(now.plusSeconds(crime.getCooldownSeconds()));

            prisonService.save(prison);

            PrisonHistory prisonHistory = PrisonHistory.builder()
                    .player(player)
                    .reason(crime.getName())
                    .dateOfArrest(now)
                    .releaseForecast(prison.getReleaseAt())
                    .build();

            prisonHistoryService.save(prisonHistory);

            return """
                    🚓 **Crime falhou**
                    
                    A polícia chegou antes de você fugir.
                    
                    ⏱️ Tempo de prisão: %ds
                    """.formatted(crime.getCooldownSeconds());
        }

        return "❌ Crime falhou. Nenhum lucro hoje.";
    }
}
