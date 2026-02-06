package top.leonam.hotbctgamess.service.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.service.PlayerService;

@Service
@Slf4j
@AllArgsConstructor
public class StatusCommand implements Command {
    private PlayerService playerService;

    @Override
    public String name() {
        return "?status";
    }

    @Override
    public String execute(MessageReceivedEvent event) {
        long id;

        var listUsers = event.getMessage().getMentions().getUsers();

        id = event.getAuthor().getIdLong();

        if(!listUsers.isEmpty()){
            if (listUsers.size() > 1) return "Você só pode ver o status de uma pessoa por vez.";
            if(listUsers.getFirst().isBot()) return "Bot's não tem Status";

            id = listUsers.getFirst().getIdLong();
            playerService.registerIfAbsent(id, listUsers.getFirst().getName());
        }

        return playerService.statusPlayer(id);
    }
}
