package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.entities.ScheduledEventImpl;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.exceptions.UserNotFound;
import top.leonam.hotbctgamess.model.entity.Account;
import top.leonam.hotbctgamess.model.entity.Identity;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.model.entity.Prison;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.repository.PlayerRepository;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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

                    Account account = Account.builder()
                            .player(newPlayer)
                            .build();

                    Prison prison = Prison.builder()
                            .player(newPlayer)
                            .status(PrisonStatus.SOLTO)
                            .build();

                    newPlayer.setAccount(account);
                    newPlayer.setPrison(prison);
                    return playerRepository.save(newPlayer);
                });
    }

    private void register(Long id,String name) {
        savePlayer(id, name);
    }

    public Player getPlayer(Long idDiscord){
        return playerRepository.findByIdentity_DiscordId(idDiscord).orElseThrow(()-> new UserNotFound("Este usuário não foi encontrado"));
    }

    @Transactional
    public String statusPlayer(Long idDiscord) {

        Player player = playerRepository.findByIdentity_DiscordId(idDiscord)
                .orElseThrow(() -> new UserNotFound("Usuário não foi localizado, porém já deveria ter sido criado automaticamente."));

        return """
            🎮 **Status Gamer**
            
            🌟 Nível Atual: %d
            ✨ XP Atual: %d
            💰 Dinheiro: R$%.2f
            💪 Pontos de Respeito: %d
            🏆 Conquistas: %d
            🧾 Crimes Registrados: %d
            🎒 Itens no Inventário: %d
            🏛 Preso?: %s
            
            """.formatted(
                player.getCurrentLevel(),
                player.getCurrentXp(),
                player.getAccount().getBalance().doubleValue(),
                player.getRespectPoints(),
                player.getAchievements() != null ? player.getAchievements().size() : 0,
                player.getCrimeHistories() != null ? player.getCrimeHistories().size() : 0,
                player.getInventorys() != null ? player.getInventorys().size() : 0,
                !player.getPrison().getStatus().name().equals(PrisonStatus.SOLTO.name()) ? "Sim" : "Não"

        );
    }

    public void save(Player player) {
        playerRepository.save(player);
    }

    public void saveAll(List<Player> players) {
        playerRepository.saveAll(players);
    }

    public void addXp(Player player, long amount) {
        long newXp = Math.max(0, player.getCurrentXp() + amount);
        player.setCurrentXp(newXp);

        updateLevel(player);

        save(player);
    }

    private long xpRequired(int level) {
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

                    Account account = Account.builder()
                            .player(newPlayer)
                            .build();

                    Prison prison = Prison.builder()
                            .player(newPlayer)
                            .status(PrisonStatus.SOLTO)
                            .build();

                    newPlayer.setAccount(account);
                    newPlayer.setPrison(prison);
                    return playerRepository.save(newPlayer);
                });
    }
}