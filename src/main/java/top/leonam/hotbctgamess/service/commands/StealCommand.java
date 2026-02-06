package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.exceptions.UserNotFound;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.*;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.model.enums.TypeTransaction;
import top.leonam.hotbctgamess.service.*;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class StealCommand implements Command {

    private final PlayerService playerService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CrimeService crimeService;
    private final CrimeHistoryService crimeHistoryService;
    private final PrisonHistoryService prisonHistoryService;
    private final PrisonService prisonService;

    @Override
    public String name() {
        return "?roubar";
    }

    @Override
    @Transactional
    public EmbedBuilder execute(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        Long idFirst = event.getAuthor().getIdLong();
        var player = playerService.getPlayer(idFirst);

        prisonService.checkAndRelease(player);

        if (player.getPrison().getStatus() == PrisonStatus.PRESO) {
            return embed.setColor(Color.RED)
                    .setTitle("🔒 Acesso Negado")
                    .setDescription("Você está preso e não consegue bater carteiras de dentro da cela.");
        }

        var listUsers = event.getMessage().getMentions().getUsers();

        if (listUsers.isEmpty()) {
            return embed.setColor(Color.ORANGE)
                    .setTitle("❓ Alvo não encontrado")
                    .setDescription("Você precisa marcar alguém para roubar.");
        }
        if (listUsers.size() > 1) {
            return embed.setColor(Color.YELLOW)
                    .setTitle("⚠️ Muita calma")
                    .setDescription("Você só tem duas mãos. Roube **uma pessoa por vez**.");
        }
        if (listUsers.getFirst().isBot()) {
            return embed.setColor(new Color(155, 89, 182))
                    .setTitle("🤖 Erro de Sistema")
                    .setDescription("Roubar bots não vale a pena. O dinheiro deles é virtual demais para as suas mãos.");
        }

        Long idLast = listUsers.getFirst().getIdLong();

        if (idFirst.equals(idLast)) {
            return embed.setColor(Color.PINK)
                    .setTitle("🪞 Crise Existencial")
                    .setDescription("Roubar a si mesmo? Isso se chama pagar boletos. Não conta como crime.");
        }

        playerService.registerIfAbsent(idLast, listUsers.getFirst().getName());

        Player playerTwo;
        try {
            playerTwo = playerService.getPlayer(idLast);
        } catch (UserNotFound e) {
            return embed.setColor(Color.RED).setDescription("👤 Esse usuário ainda não faz parte do jogo.");
        }

        Crime crime = crimeService.getCrimeByName("batercarteira");
        BigDecimal potentialGain = BigDecimal.valueOf(CrimeUtils.randomValueCrime(crime));
        boolean success = CrimeUtils.successCrime(crime);
        boolean policeArrested = !success && CrimeUtils.policeArrested(crime);
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

                embed.setColor(Color.GREEN)
                        .setTitle("💰 ROUBO BEM-SUCEDIDO")
                        .setThumbnail("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExdm9lMzJleXF1d2R0dzkxZmFsY2I0eHVud3JqcTU4cWo1dTVvdzM1NyZlcD12MV9naWZzX3NlYXJjaCZjdD1n/GboxvbUStR0hpbmJjK/giphy.gif")
                        .setDescription("Você foi furtivo e conseguiu subtrair dinheiro alheio!")
                        .addField("👤 Vítima", listUsers.getFirst().getName(), true)
                        .addField("💸 Valor", String.format("R$ %.2f", potentialGain.doubleValue()), true)
                        .addField("⭐ XP", "+" + crime.getXp(), true)
                        .setFooter("O dinheiro já foi depositado na sua conta.");
                return embed;
            }

            return embed.setColor(Color.GRAY)
                    .setTitle("💸 Vítima Lisa")
                    .setDescription(String.format("Você tentou roubar **%s**, mas ele(a) está mais quebrado(a) que você.", listUsers.getFirst().getName()));
        }

        playerService.addXp(player, -crime.getXp());
        playerService.addXp(playerTwo, +crime.getXp());

        if (policeArrested) {
            Prison prison = prisonService.getPrisonByDiscordId(idFirst);
            prison.setStatus(PrisonStatus.PRESO);
            prison.setJailedAt(dateTime);
            prison.setReleaseAt(dateTime.plusSeconds(crime.getCooldownSeconds()));
            prisonService.save(prison);

            PrisonHistory prisonHistory = PrisonHistory.builder()
                    .player(player)
                    .reason("Roubo a " + listUsers.getFirst().getName())
                    .dateOfArrest(dateTime)
                    .releaseForecast(prison.getReleaseAt())
                    .build();
            prisonHistoryService.save(prisonHistory);

            return embed.setColor(Color.RED)
                    .setTitle("🚓 ROUBO FRACASSADO")
                    .setDescription("A polícia apareceu no momento exato! Você foi algemado e levado para a delegacia.")
                    .addField("⏱️ Pena", crime.getCooldownSeconds() + "s", true)
                    .addField("📉 Penalidade", "-" + crime.getXp() + " XP", true)
                    .setFooter("Sua ficha criminal acaba de ganhar um novo capítulo.");
        }

        return embed.setColor(Color.LIGHT_GRAY)
                .setTitle("❌ Quase!")
                .setDescription(String.format("Você tentou bater a carteira de %s, mas ele percebeu e você teve que fugir sem nada!", listUsers.getFirst().getName()))
                .addField("📉 Penalidade", "-" + crime.getXp() + " XP", false);
    }
}