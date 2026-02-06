package top.leonam.hotbctgamess.service.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;

@Service
@Slf4j
public class EstouRindoCommand implements Command {

    @Override
    public String name() {
        return "?estourindo";
    }

    @Override
    public String execute(MessageReceivedEvent event) {
        return "Sim";
    }
}
