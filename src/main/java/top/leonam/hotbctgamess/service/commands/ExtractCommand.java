package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.service.PlayerService;
import top.leonam.hotbctgamess.service.PrisonService;
import top.leonam.hotbctgamess.service.TransactionService;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Slf4j
@AllArgsConstructor
public class ExtractCommand implements Command {
    private TransactionService transactionService;
    private PlayerService playerService;
    private PrisonService prisonService;

    @Override
    public String name() {
        return "?extrato";
    }
    @Transactional
    @Override
    public String execute(MessageReceivedEvent event) {
        Long id = event.getAuthor().getIdLong();

        var player = playerService.getPlayer(id);

        prisonService.checkAndRelease(player);

        if (player.getPrison().getStatus() == PrisonStatus.PRESO) return "🔒 Você ainda está preso. Aguarde o tempo acabar ou pague a fiança.";

        String content = event.getMessage().getContentRaw();
        String[] parts = content.split("\\s+");

        if (parts.length < 2) return transactionService.getExtract(id);

        try {
            String dateInput = parts[parts.length - 1];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            YearMonth yearMonth = YearMonth.parse(dateInput, formatter);
            LocalDateTime dateTime = yearMonth.atDay(1).atStartOfDay();



            if (player.getPrison().getStatus() == PrisonStatus.PRESO) return "🔒 Você ainda está preso. Aguarde o tempo acabar ou pague a fiança.";

            return transactionService.getExtract(id, dateTime);

        } catch (DateTimeParseException e) {
            return "Formato de data inválido! Use: `?extrato 02/2026`";
        }
    }
}
