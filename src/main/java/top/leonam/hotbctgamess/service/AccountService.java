package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.exceptions.UserNotFound;
import top.leonam.hotbctgamess.model.entity.Account;
import top.leonam.hotbctgamess.repository.AccountRepository;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public EmbedBuilder walletStats(MessageReceivedEvent event) {
        var idDiscord = event.getAuthor().getIdLong();

        Account account = accountRepository
                .findByPlayer_Identity_DiscordId(idDiscord)
                .orElseThrow(() -> {
                    log.info("Discord ID: {}", idDiscord);
                    log.info("O comando foi: {}", event.getMessage());
                    return new UserNotFound("Usuário não foi localizado.");
                });

        EmbedBuilder embed = getEmbedBuilder(event, account);

        embed.setFooter("Consulta realizada em: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")), null);

        return embed;
    }

    private static @NonNull EmbedBuilder getEmbedBuilder(MessageReceivedEvent event, Account account) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🏦 Central Bank - " + event.getAuthor().getName());
        embed.setColor(new Color(41, 128, 185));

        embed.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());


        embed.setDescription("Aqui estão os detalhes da sua conta bancária oficial.");

        embed.addField("💰 Saldo Disponível",
                String.format("```fix\nR$ %.2f\n```", account.getBalance().doubleValue()),
                false);

        embed.addField("💳 Tipo de Conta", "VIP Platinum", true);
        embed.addField("📍 Status", "Ativa", true);
        return embed;
    }

    @Transactional
    public Account getAccountByDiscordId(Long discordId) {
        return accountRepository
                .findByPlayer_Identity_DiscordId(discordId)
                .orElseThrow(() -> {
                    log.info("Discord ID: {}", discordId);
                    return new UserNotFound("Usuário não foi localizado.");
                });
    }
}