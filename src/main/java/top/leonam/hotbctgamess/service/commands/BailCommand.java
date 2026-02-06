package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.service.PrisonService;

@Service
@Slf4j
@AllArgsConstructor
public class BailCommand implements Command {
    private PrisonService prisonService;
    @Override
    public String name() {
        return "?pagarfianca";
    }

    @Override
    @Transactional
    public EmbedBuilder execute(MessageReceivedEvent event) {
        return prisonService.payBail(event);
    }
}
