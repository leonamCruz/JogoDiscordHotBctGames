package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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

@Service
@AllArgsConstructor
@Slf4j
public class StealCommand implements Command {

    private PlayerService playerService;
    private AccountService accountService;
    private TransactionService transactionService;
    private CrimeService crimeService;
    private CrimeHistoryService crimeHistoryService;
    private PrisonHistoryService prisonHistoryService;
    private PrisonService prisonService;

    @Override
    public String name() {
        return "?roubar";
    }

    @Override
    @Transactional
    public String execute(MessageReceivedEvent event) {
        Long idFirst = event.getAuthor().getIdLong();

        var player = playerService.getPlayer(idFirst);

        prisonService.checkAndRelease(player);

        if (player.getPrison().getStatus() == PrisonStatus.PRESO) return "🔒 Você ainda está preso. Aguarde o tempo acabar ou pague a fiança.";

        var listUsers = event.getMessage().getMentions().getUsers();

        if (listUsers.isEmpty()) return "❌ Você precisa marcar alguém para roubar.";
        if (listUsers.size() > 1) return "⚠️ Calma lá. Só dá pra roubar **uma pessoa por vez**.";
        if (listUsers.getFirst().isBot()) return "🤖 Roubar bot não vale. Eles não sentem dor nem perdem dinheiro.";

        Long idLast = listUsers.getFirst().getIdLong();

        if (idFirst.equals(idLast)) return "🪞 Roubar a si mesmo não conta como crime. Conta como terapia.";

        playerService.registerIfAbsent(idLast, listUsers.getFirst().getName());
        Guild guild = event.getGuild();

//        Member member = guild.getMemberById(idLast);
//
//        if (member == null) {
//            try {
//                member = guild.retrieveMemberById(idLast).complete();
//            } catch (Exception e) {
//                return "🚫 Esse jogador não está neste servidor.";
//            }
//        }
//
//        if (member.getOnlineStatus() == OnlineStatus.OFFLINE) {
//            return "💤 Esse jogador está offline.";
//        }

        Crime crime = crimeService.getCrimeByName("batercarteira");

        BigDecimal potentialGain = BigDecimal.valueOf(CrimeUtils.randomValueCrime(crime));

        boolean success = CrimeUtils.successCrime(crime);
        boolean policeArrested = !success && CrimeUtils.policeArrested(crime);

        Player playerTwo;
        try {
            playerTwo = playerService.getPlayer(idLast);
        } catch (UserNotFound e) {
            return "👤 Esse usuário ainda não faz parte do jogo.";
        }

        LocalDateTime dateTime = LocalDateTime.now();

        CrimeHistory crimeHistory = CrimeHistory.builder()
                .player(player)
                .crime(crime)
                .attemptedAt(dateTime)
                .success(success)
                .reward(potentialGain)
                .jailed(policeArrested)
                .jailTimeSeconds(crime.getCooldownSeconds())
                .build();

        crimeHistoryService.save(crimeHistory);

        Account fromAccount = accountService.getAccountByDiscordId(idLast);
        Account toAccount = accountService.getAccountByDiscordId(idFirst);

        if (success && !policeArrested) {
            if (transactionService.transfer(fromAccount, toAccount, potentialGain, TypeTransaction.ROUBO)) {
                playerService.addXp(player, +crime.getXp());
                playerService.addXp(playerTwo, -crime.getXp());

                return String.format("""
                                💰 **ROUBO BEM-SUCEDIDO**
                                
                                Você roubou **R$ %.2f** de **%s**.
                                
                                O dinheiro já caiu na sua conta.
                                """,
                        potentialGain.doubleValue(),
                        listUsers.getFirst().getName()
                );

            }
            return "💸 A vítima está mais lisa que você.\nTente roubar alguém com dinheiro de verdade.";
        }

        playerService.addXp(player, -crime.getXp());
        playerService.addXp(playerTwo, +crime.getXp());

        PrisonHistory prisonHistory = PrisonHistory.builder()
                .player(player)
                .reason("Roubo")
                .dateOfArrest(dateTime)
                .releaseForecast(dateTime.plusSeconds(crime.getCooldownSeconds()))
                .build();

        prisonHistoryService.save(prisonHistory);

        Prison prison = prisonService.getPrisonByDiscordId(idFirst);

        prison.setStatus(PrisonStatus.PRESO);
        prison.setJailedAt(dateTime);
        prison.setReleaseAt(dateTime.plusSeconds(crime.getCooldownSeconds()));


        prisonService.save(prison);

        return """
                🚓 **ROUBO FRACASSADO**
                
                A polícia apareceu antes de você fugir.
                Você foi preso.
                
                ⏱️ Tempo de prisão: %ds
                """.formatted(crime.getCooldownSeconds());

    }




}
