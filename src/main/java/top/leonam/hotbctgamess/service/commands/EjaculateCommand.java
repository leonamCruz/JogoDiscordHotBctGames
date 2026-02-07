package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.enums.PrisonStatus;
import top.leonam.hotbctgamess.service.EggService;
import top.leonam.hotbctgamess.service.EjaculateService;
import top.leonam.hotbctgamess.service.PlayerService;
import top.leonam.hotbctgamess.service.PrisonService;

import java.awt.Color;

@Service
@Slf4j
@AllArgsConstructor
public class EjaculateCommand implements Command {
    private PlayerService playerService;
    private PrisonService prisonService;
    private EjaculateService ejaculateService;
    private EggService eggService;

    @Override
    public String name() {
        return "?gozar";
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        Long idFirst = event.getAuthor().getIdLong();

        var player = playerService.getPlayer(idFirst);
        prisonService.checkAndRelease(player);

        if (player.getPrison().getStatus() == PrisonStatus.PRESO) {
            embed.setColor(Color.RED);
            embed.setTitle("🔒 Bloqueado");
            embed.setDescription("Você está preso. Não dá pra fazer isso de dentro da cela (pelo menos não com os outros).");
            return embed;
        }

        var listUsers = event.getMessage().getMentions().getUsers();

        if (listUsers.isEmpty()) {
            embed.setColor(Color.ORANGE);
            embed.setTitle("❓ Quem é o alvo?");
            embed.setDescription("Você precisa marcar alguém para realizar o ato.");
            return embed;
        }

        if (listUsers.size() > 1) {
            embed.setColor(Color.YELLOW);
            embed.setTitle("⚠️ Calma lá, garanhão");
            embed.setDescription("Só dá para gozar em **uma pessoa por vez**. Foco no objetivo.");
            return embed;
        }

        if (listUsers.getFirst().isBot()) {
            embed.setColor(new Color(155, 89, 182));
            embed.setTitle("🤖 Erro de Hardware");
            embed.setDescription("Gozar no bot não vale. Eles não têm sentimentos e os circuitos ficam em curto.");
            return embed;
        }

        Long idLast = listUsers.getFirst().getIdLong();

        if (idFirst.equals(idLast)) {
            embed.setColor(Color.PINK);
            embed.setTitle("🪞 Autoconhecimento");
            embed.setDescription("Gozar em si mesmo é um ato profano. Conta como terapia, não como crime/ação.");
            return embed;
        }

        playerService.registerIfAbsent(idLast, listUsers.getFirst().getName());

        Integer quantity = eggService.getAmmountOfEjaculateRemaining(player);
        log.info(quantity.toString());
        if(quantity <= 0){
            embed.setColor(Color.ORANGE);
            embed.setTitle("🔒 Bloqueado");
            embed.setThumbnail("https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExN3VvMjh2bmZ3NHViYjBqNWh1ZnR4b3M5eXNqb29ob3I4MHhnbGszeSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/S9FtQO5fxOMsT5l1wa/giphy.gif");
            embed.setDescription("Você não pode mais gozar hoje. Está com o saco inflamado, goze amanhã \uD83D\uDE00");

            return embed;
        }

        var playerTwo = playerService.getPlayer(idLast);

        return ejaculateService.ejaculateIn(player, playerTwo, event);
    }
}