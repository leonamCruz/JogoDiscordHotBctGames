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
import top.leonam.hotbctgamess.model.entity.Account;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.model.enums.TypeTransaction;
import top.leonam.hotbctgamess.service.AccountService;
import top.leonam.hotbctgamess.service.PlayerService;
import top.leonam.hotbctgamess.service.PrisonService;
import top.leonam.hotbctgamess.service.TransactionService;

import java.math.BigDecimal;

@Service
@Slf4j
@AllArgsConstructor
public class TransferCommand implements Command {
    private PlayerService playerService;
    private PrisonService prisonService;
    private TransactionService transactionService;
    private AccountService accountService;

    @Override
    public String name() {
        return "?pix";
    }

    @Override
    @Transactional
    public String execute(MessageReceivedEvent event) {
        Long idFirst = event.getAuthor().getIdLong();
        var listUsers = event.getMessage().getMentions().getUsers();

        Player player = playerService.getPlayer(idFirst);
        prisonService.checkAndRelease(player);

        if (player.getPrison().getStatus() == PrisonStatus.PRESO) {
            return "🔒 Você ainda está preso. Aguarde o tempo acabar ou pague a fiança.";
        }

        if (listUsers.isEmpty()) return "Marque alguém pra fazer o Pix";
        if (listUsers.size() > 1) return "Você só pode transferir para uma pessoa por vez.";
        if (listUsers.getFirst().isBot()) return "Você não pode fazer Pix para Bot's";

        Long idLast = listUsers.getFirst().getIdLong();
        if (idFirst.equals(idLast)) return "Você não pode fazer pix para si.";

        playerService.registerIfAbsent(idLast, listUsers.getFirst().getName());

//        Guild guild = event.getGuild();

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

        String content = event.getMessage().getContentRaw();
        String[] parts = content.split("\\s+");
        BigDecimal amount;

        try {
            String lastPart = parts[parts.length - 1].replace(",", ".");

            amount = new BigDecimal(lastPart);

            if (amount.scale() > 2) return "Valor inválido! O Pix só aceita até duas casas decimais (centavos).";
            if (amount.compareTo(BigDecimal.ZERO) <= 0) return "O valor do Pix deve ser maior que zero.";
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return "Valor inválido! Use o formato: `?pix @usuario 200.55`";
        }

        Account fromAccount = accountService.getAccountByDiscordId(idFirst);
        Account toAccount;
        try {
            toAccount = accountService.getAccountByDiscordId(idLast);
        } catch (UserNotFound e) {
            return "Essa pessoa ainda não joga conosco.";
        }

        boolean success = transactionService.transfer(fromAccount, toAccount, amount, TypeTransaction.PIX);

        if (!success) return "Você não tem saldo suficiente para esse Pix.";

        return String.format("Sucesso! Você enviou **R$ %.2f** para %s.",
                amount.doubleValue(),
                listUsers.getFirst().getName());
    }
}
