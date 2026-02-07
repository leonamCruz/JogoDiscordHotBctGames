package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.exceptions.UserNotFound;
import top.leonam.hotbctgamess.model.entity.*;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.repository.PlayerRepository;

import java.awt.*;

@Service
@AllArgsConstructor
@Slf4j
public class PlayerService {
    private PlayerRepository playerRepository;

    @Transactional
    public void registerIfAbsent(MessageReceivedEvent event) {
        register(event);
    }

    @Transactional
    public void registerIfAbsent(Long id, String name) {
        register(id, name);
    }

    private void register(MessageReceivedEvent event) {
        long id = event.getAuthor().getIdLong();
        String name = event.getAuthor().getName();
        savePlayer(id, name);
    }
    public void save(Player player) {
        playerRepository.save(player);
    }

    public void registerStateIfAbsent(long botId, String estado) {
        playerRepository.findByIdentity_DiscordId(botId)
                .orElseGet(() -> {

                    Identity identity = Identity.builder()
                            .discordId(botId)
                            .name(estado)
                            .build();

                    Player newPlayer = Player.builder()
                            .identity(identity)
                            .currentLevel(1_000_000)
                            .currentXp(0L)
                            .respectPoints(1_000_000)
                            .build();

                    Egg egg = Egg.builder()
                            .remainingQuantity(3)
                            .inflamed(false)
                            .player(newPlayer)
                            .build();

                    Account account = Account.builder()
                            .player(newPlayer)
                            .build();

                    Prison prison = Prison.builder()
                            .player(newPlayer)
                            .build();

                    newPlayer.setEgg(egg);
                    newPlayer.setAccount(account);
                    newPlayer.setPrison(prison);

                    return playerRepository.saveAndFlush(newPlayer);
                });
    }
    private void savePlayer(long id, String name) {
        playerRepository.findByIdentity_DiscordId(id)
                .orElseGet(() -> {

                    Identity identity = Identity.builder()
                            .discordId(id)
                            .name(name)
                            .build();

                    Player newPlayer = Player.builder()
                            .identity(identity)
                            .currentLevel(1)
                            .currentXp(0L)
                            .respectPoints(0)
                            .build();

                    Egg egg = Egg.builder()
                            .remainingQuantity(3)
                            .inflamed(false)
                            .player(newPlayer)
                            .build();

                    newPlayer.setEgg(egg);

                    Account account = Account.builder()
                            .player(newPlayer)
                            .build();

                    Prison prison = Prison.builder()
                            .player(newPlayer)
                            .build();

                    newPlayer.setAccount(account);
                    newPlayer.setPrison(prison);

                    return playerRepository.saveAndFlush(newPlayer);
                });
    }

    private void register(Long id,String name) {
        savePlayer(id, name);
    }

    @Transactional
    public Player getPlayer(Long idDiscord){
        return playerRepository.findByIdentity_DiscordId(idDiscord).orElseThrow(()-> new UserNotFound("Este usuário não foi encontrado"));
    }
    @Transactional
    public EmbedBuilder statusPlayer(Long idDiscord, String avatarUrl) {
        Player player = playerRepository.findByIdentity_DiscordId(idDiscord)
                .orElseThrow(() -> new UserNotFound("Usuário não foi localizado."));

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("👤 Status do Jogador: " + player.getIdentity().getName());
        embed.setColor(new Color(46, 204, 113));

        embed.setThumbnail(avatarUrl);

        int level = player.getCurrentLevel();
        long currentXp = player.getCurrentXp();
        long nextLevelXp = xpRequired(level + 1);
        long currentLevelXp = xpRequired(level);

        double progress = (double) (currentXp - currentLevelXp) / (nextLevelXp - currentLevelXp);
        String progressBar = makeProgressBar(progress);

        // --- Campos do Embed ---
        embed.addField("📊 Progressão",
                String.format("⭐ **Nível:** `%d`\n✨ **XP:** `%d / %d`\n%s",
                        level, currentXp, nextLevelXp, progressBar), false);

        embed.addField("💰 Dinheiro",
                String.format("`R$ %.2f`", player.getAccount().getBalance().doubleValue()), true);

        embed.addField("💪 Respeito",
                String.format("`%d pts`", player.getRespectPoints()), true);

        int crimes = player.getCrimeHistories() != null ? player.getCrimeHistories().size() : 0;
        String estaPreso = !player.getPrison().getStatus().equals(PrisonStatus.SOLTO) ? "🔴 Sim" : "🟢 Não";

        embed.addField("⚖️ Ficha Criminal",
                String.format("📑 **Crimes:** `%d`\n⛓️ **Preso agora:** %s", crimes, estaPreso), true);

        int conquistas = player.getAchievements() != null ? player.getAchievements().size() : 0;
        int itens = player.getInventorys() != null ? player.getInventorys().size() : 0;

        embed.addField("🎒 Posses",
                String.format("🏆 **Conquistas:** `%d`\n📦 **Itens:** `%d`", conquistas, itens), true);

        embed.setFooter("ID: " + idDiscord, null);

        return embed;
    }
    public void addXp(Player player, long amount) {
        long newXp = Math.max(0, player.getCurrentXp() + amount);
        player.setCurrentXp(newXp);

        updateLevel(player);

        save(player);
    }

    private String makeProgressBar(double fraction) {
        int totalBars = 10;
        int filledBars = (int) (fraction * totalBars);
        StringBuilder sb = new StringBuilder("`[");
        for (int i = 0; i < totalBars; i++) {
            if (i < filledBars) sb.append("▰");
            else sb.append("▱");
        }
        sb.append("]` ").append((int) (fraction * 100)).append("%");
        return sb.toString();
    }

    private long xpRequired(int level) {
        if (level <= 0) return 0;
        return 100L * level * level;
    }

    private void updateLevel(Player player) {
        long xp = player.getCurrentXp();
        int level = 0;

        while (xp >= xpRequired(level + 1)) {
            level++;
        }

        player.setCurrentLevel(level);
    }

}