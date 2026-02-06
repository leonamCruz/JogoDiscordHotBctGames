package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
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

import java.awt.Color;
import java.math.BigDecimal;

@Service
@Slf4j
@AllArgsConstructor
public class TransferCommand implements Command {
    private final PlayerService playerService;
    private final PrisonService prisonService;
    private final TransactionService transactionService;
    private final AccountService accountService;

    @Override
    public String name() {
        return "?pix";
    }

    @Override
    @Transactional
    public EmbedBuilder execute(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        Long idFirst = event.getAuthor().getIdLong();
        var listUsers = event.getMessage().getMentions().getUsers();

        Player player = playerService.getPlayer(idFirst);
        prisonService.checkAndRelease(player);

        // 1. Verificação de Prisão
        if (player.getPrison().getStatus() == PrisonStatus.PRESO) {
            return embed.setColor(Color.RED)
                    .setTitle("🔒 Bloqueado")
                    .setDescription("Sua conta está congelada enquanto você estiver preso. Saia da cadeia para movimentar dinheiro.");
        }

        // 2. Validações de Alvo
        if (listUsers.isEmpty()) {
            return embed.setColor(Color.ORANGE).setDescription("❓ Marque alguém para fazer o Pix. Ex: `?pix @usuario 50`.");
        }
        if (listUsers.size() > 1) {
            return embed.setColor(Color.ORANGE).setDescription("⚠️ Você só pode transferir para uma pessoa por vez.");
        }
        if (listUsers.getFirst().isBot()) {
            return embed.setColor(new Color(155, 89, 182)).setDescription("🤖 Bots não possuem contas bancárias.");
        }

        Long idLast = listUsers.getFirst().getIdLong();
        if (idFirst.equals(idLast)) {
            return embed.setColor(Color.PINK).setDescription("🪞 Transferir para si mesmo não muda seu saldo.");
        }

        playerService.registerIfAbsent(idLast, listUsers.getFirst().getName());

        // 3. Validação do Valor
        String content = event.getMessage().getContentRaw();
        String[] parts = content.split("\\s+");
        BigDecimal amount;

        try {
            String lastPart = parts[parts.length - 1].replace(",", ".");
            amount = new BigDecimal(lastPart);

            if (amount.scale() > 2) {
                return embed.setColor(Color.RED).setDescription("❌ Valor inválido! Use no máximo duas casas decimais.");
            }
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return embed.setColor(Color.RED).setDescription("❌ O valor do Pix deve ser maior que zero.");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return embed.setColor(Color.WHITE)
                    .setTitle("❓ Como usar o Pix")
                    .setDescription("Formato correto: `?pix @usuario <valor>`\nExemplo: `?pix @Leonam 250.00`.");
        }

        Account fromAccount = accountService.getAccountByDiscordId(idFirst);
        Account toAccount;
        try {
            toAccount = accountService.getAccountByDiscordId(idLast);
        } catch (UserNotFound e) {
            return embed.setColor(Color.RED).setDescription("👤 Essa pessoa ainda não possui uma conta no banco.");
        }

        boolean success = transactionService.transfer(fromAccount, toAccount, amount, TypeTransaction.PIX);

        if (!success) {
            return embed.setColor(Color.RED)
                    .setTitle("❌ Saldo Insuficiente")
                    .setDescription(String.format("Você não tem **R$ %.2f** para realizar essa transferência.", amount.doubleValue()));
        }

        embed.setColor(new Color(38, 186, 172))
                .setTitle("✅ Pix Realizado com Sucesso")
                .setThumbnail("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExNTJzYmhueG4zY2Y0ajJndnFjbWY4NXF2aHB2azJtZzRtN3l1dm10NyZlcD12MV9naWZzX3NlYXJjaCZjdD1n/X8WNVwXFnYWUMFnI4z/giphy.gif")
                .addField("Enviado para", listUsers.getFirst().getAsMention(), true)
                .addField("Valor", String.format("`R$ %.2f`", amount.doubleValue()), true)
                .addField("Status", "Finalizado", true)
                .setFooter("Comprovante gerado em: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        return embed;
    }
}