package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.service.PlayerService;
import top.leonam.hotbctgamess.service.PrisonService;
import top.leonam.hotbctgamess.service.TransactionService;

import java.awt.Color;

@Service
@Slf4j
@AllArgsConstructor
public class ExtractCommand implements Command {
    private final TransactionService transactionService;
    private final PlayerService playerService;
    private final PrisonService prisonService;

    @Override
    public String name() {
        return "?extrato";
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        Long id = event.getAuthor().getIdLong();
        var player = playerService.getPlayer(id);

        prisonService.checkAndRelease(player);

        if (player.getPrison().getStatus() == PrisonStatus.PRESO) {
            EmbedBuilder errorEmbed = new EmbedBuilder();
            errorEmbed.setColor(Color.RED);
            errorEmbed.setTitle("🔒 Acesso Negado");
            errorEmbed.setDescription("Presos não têm acesso ao extrato bancário. Pague sua fiança primeiro!");
            return errorEmbed;
        }

        return transactionService.getExtract(id);
    }
}