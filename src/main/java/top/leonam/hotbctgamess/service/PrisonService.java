package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.*;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.model.enums.TypeTransaction;
import top.leonam.hotbctgamess.repository.PrisonRepository;

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
    public String payBail(MessageReceivedEvent event) {

        Long idDiscord = event.getAuthor().getIdLong();
        Long stateId = event.getJDA().getSelfUser().getIdLong();

        Prison prison = getPrisonByDiscordId(idDiscord);

        if (prison.getStatus() == PrisonStatus.SOLTO) {
            return "🟢 Você já está solto. O Estado agradece a tentativa de doação.";
        }

        CrimeHistory lastCrime = crimeHistoryService.getLastCrime(idDiscord);
        if (lastCrime == null) return "❓ Nenhum crime registrado. Algo aqui cheira a bug.";

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
            return String.format(
                    """
                    🚫 Fiança recusada.
                    
                    💰 Valor exigido: **R$ %.2f**
                    📉 Saldo insuficiente.
                    
                    Continue refletindo atrás das grades.
                    """,
                    bailValue.doubleValue()
            );
        }

        playerService.addXp(
                player,
                -Math.min(player.getCurrentXp(), lastCrime.getCrime().getXp())
        );

        prison.setStatus(PrisonStatus.SOLTO);
        prison.setReleaseAt(LocalDateTime.now());
        prison.setJailedAt(null);

        prisonRepository.save(prison);

        return String.format(
                """
                🏛️ Fiança paga com sucesso.
                
                💸 Valor pago: **R$ %.2f**
                📉 XP penalizado: **-%d**
                
                🔓 Você está livre.
                Tente não voltar tão cedo.
                """,
                bailValue.doubleValue(),
                lastCrime.getCrime().getXp()
        );
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
