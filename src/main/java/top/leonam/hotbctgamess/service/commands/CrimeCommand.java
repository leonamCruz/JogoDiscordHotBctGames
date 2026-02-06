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
    public EmbedBuilder execute(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        long playerId = event.getAuthor().getIdLong();
        Player player;

        try {
            player = playerService.getPlayer(playerId);
        } catch (UserNotFound e) {
            embed.setColor(Color.RED);
            embed.setTitle("👤 Jogador não encontrado");
            embed.setDescription("Você ainda não está registrado no jogo. Use `?registrar` para começar.");
            return embed;
        }

        prisonService.checkAndRelease(player);

        if (player.getPrison().getStatus() == PrisonStatus.PRESO) {
            embed.setColor(Color.ORANGE);
            embed.setTitle("🔒 Acesso Negado");
            embed.setDescription("Você está atrás das grades. Aguarde sua pena ou pague a fiança.");
            embed.setFooter("Status: " + player.getPrison().getStatus());
            return embed;
        }

        String[] parts = event.getMessage().getContentRaw().split("\\s+");

        if (parts.length != 2) {
            embed.setColor(Color.WHITE);
            embed.setTitle("❓ Como usar?");
            embed.setDescription("Uso correto: `?cometer <nome_do_crime>`");
            return embed;
        }

        String crimeName = parts[1].toLowerCase();
        Crime crime;

        try {
            crime = crimeService.getCrimeByName(crimeName);
        } catch (NoSuchElementException e) {
            embed.setColor(Color.RED);
            embed.setTitle("🚫 Crime Inexistente");
            embed.setDescription("O crime `" + crimeName + "` não consta nos nossos registros criminais.");
            return embed;
        }

        if (player.getCurrentLevel() < crime.getMinLevel()) {
            embed.setColor(Color.RED);
            embed.setTitle("🚫 Nível Insuficiente");
            embed.addField("🔓 Necessário", "Nível " + crime.getMinLevel(), true);
            embed.addField("📉 Seu nível", String.valueOf(player.getCurrentLevel()), true);
            embed.setDescription("Você ainda é muito inexperiente para este crime.");
            return embed;
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

            embed.setColor(Color.GREEN);
            embed.setTitle("🔪 Crime Bem-Sucedido!");
            embed.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());
            embed.addField("🧾 Crime", crime.getName(), true);
            embed.addField("💰 Lucro", String.format("R$ %.2f", reward.doubleValue()), true);
            embed.addField("⭐ XP ganho", "+" + crime.getXp(), true);
            embed.setFooter("Sua ficha criminal acaba de crescer...");
            return embed;
        }

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

            embed.setColor(Color.RED);
            embed.setTitle("🚓 PERDEMO! A POLÍCIA CHEGOU!");
            embed.setDescription("Você tentou ser esperto, mas o camburão chegou antes.");
            embed.addField("⏱️ Pena", crime.getCooldownSeconds() + " segundos", true);
            embed.addField("📉 Penalidade", "-" + crime.getXp() + " XP", true);
            embed.setImage("https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExbnpwa3o2MXI2aHo2NnNmbjVlYW90eXczNHF6NjEzbGkxMWRpMDZxZyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/rxKRbfKh8WXtjxW8vl/giphy.gif");
            return embed;
        }

        embed.setColor(Color.GRAY);
        embed.setTitle("❌ Não Conseguiu!");
        embed.setDescription("Você não conseguiu completar o crime, mas pelo menos fugiu a tempo.");
        embed.addField("📉 Penalidade", "-" + crime.getXp() + " XP", true);
        return embed;
    }
}