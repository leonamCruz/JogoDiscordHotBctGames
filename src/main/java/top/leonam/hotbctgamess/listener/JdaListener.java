package top.leonam.hotbctgamess.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.SessionResumeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NonNull;
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
            log.info("Comando executado: {}", command.name());

            service.registerIfAbsent(event);

            String message = command.execute(event);
            event.getMessage().reply(message).queue();
        }
    }
}

