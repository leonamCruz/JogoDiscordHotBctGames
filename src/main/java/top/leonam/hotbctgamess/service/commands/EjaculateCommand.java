package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.service.EjaculateService;
import top.leonam.hotbctgamess.service.PlayerService;
import top.leonam.hotbctgamess.service.PrisonService;

@Service
@Slf4j
@AllArgsConstructor
public class EjaculateCommand implements Command {
    private PlayerService playerService;
    private PrisonService prisonService;
    private EjaculateService ejaculateService;

    @Override
    public String name() {
        return "?gozar";
    }

    @Transactional
    @Override
    public String execute(MessageReceivedEvent event) {
        Long idFirst = event.getAuthor().getIdLong();

        var player = playerService.getPlayer(idFirst);

        prisonService.checkAndRelease(player);

        if (player.getPrison().getStatus() == PrisonStatus.PRESO) return "🔒 Você ainda está preso. Aguarde o tempo acabar ou pague a fiança.";

        var listUsers = event.getMessage().getMentions().getUsers();

        if (listUsers.isEmpty()) return "❌ Você precisa marcar alguém para gozar.";
        if (listUsers.size() > 1) return "⚠️ Calma lá. Só dá pra gozar em **uma pessoa por vez**.";
        if (listUsers.getFirst().isBot()) return "🤖 Gozar no bot não vale. Eles não tem orgasmo e ficam meladinhos.";

        Long idLast = listUsers.getFirst().getIdLong();

        if (idFirst.equals(idLast)) return "🪞 Gozar em si mesmo é um ato profano. Conta como terapia.";

        playerService.registerIfAbsent(idLast, listUsers.getFirst().getName());

        var playerOne = playerService.getPlayer(idFirst);
        var playerTwo = playerService.getPlayer(idLast);

        return ejaculateService.ejaculateIn(playerOne, playerTwo, event);
    }
}
