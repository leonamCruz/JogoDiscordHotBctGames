package top.leonam.hotbctgamess.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.registers.CommandRegistry;
import top.leonam.hotbctgamess.service.PlayerService;

@Component
@AllArgsConstructor
@Slf4j
public class JdaListener extends ListenerAdapter {

    private CommandRegistry registry;
    private PlayerService service;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String raw = event.getMessage().getContentRaw();
        String commandName = raw.split("\\s+")[0];

        Command command = registry.get(commandName);

        if (command != null) {
            service.registerIfAbsent(event);

            EmbedBuilder message = command.execute(event);
            event.getMessage().replyEmbeds(message.build()).queue();
        }
    }
}

