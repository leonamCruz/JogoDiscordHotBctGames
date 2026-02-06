package top.leonam.hotbctgamess.service.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.service.PlayerService;

import java.awt.Color;

@Service
@Slf4j
@AllArgsConstructor
public class StatusCommand implements Command {
    private final PlayerService playerService;

    @Override
    public String name() {
        return "?status";
    }
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        long id;
        String avatarUrl;
        var listUsers = event.getMessage().getMentions().getUsers();

        if (!listUsers.isEmpty()) {
            var target = listUsers.getFirst();

            if (listUsers.size() > 1) {
                return new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("⚠️ Você só pode ver o status de uma pessoa por vez.");
            }
            if (target.isBot()) {
                return new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("🤖 Bots não possuem status de jogador.");
            }

            id = target.getIdLong();
            avatarUrl = target.getEffectiveAvatarUrl();
            playerService.registerIfAbsent(id, target.getName());
        } else {
            id = event.getAuthor().getIdLong();
            avatarUrl = event.getAuthor().getEffectiveAvatarUrl();
        }

        return playerService.statusPlayer(id, avatarUrl);
    }
}