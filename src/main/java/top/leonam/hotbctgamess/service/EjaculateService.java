package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Ejaculate;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.model.enums.EjaculateStatus;
import top.leonam.hotbctgamess.repository.EjaculateRepository;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class EjaculateService {
    private EjaculateRepository ejaculateRepository;
    private DiscordService discordService;
    @Transactional
    public String ejaculateIn(Player playerOne, Player playerTwo, MessageReceivedEvent event) {
        Random random = new Random();
        var minutes = random.nextInt(1, 10);

        var ejaculate = Ejaculate
                .builder()
                .of(playerOne)
                .from(playerTwo)
                .whenWillItHappen(LocalDateTime.now().plusMinutes(minutes))
                .status(EjaculateStatus.PROGRESSO)
                .notified(false)
                .idChannel(event.getChannel().getIdLong())
                .build();

        ejaculateRepository.save(ejaculate);

        String mentionOne = "<@" + playerOne.getIdentity().getDiscordId() + ">";
        String mentionTwo = "<@" + playerTwo.getIdentity().getDiscordId() + ">";

        return String.format("""
            🍆💦 O(a) %s gozará no(a) %s em %s minutos;
            """, mentionOne, mentionTwo, minutes);
    }

    @Transactional
    public void verifyEjaculate() {
        Set<Ejaculate> ejaculates = ejaculateRepository.findByWhenWillItHappenBefore(LocalDateTime.now());

        for(Ejaculate e : ejaculates){
            if(!e.isNotified()){
                String mentionOf = "<@" + e.getOf().getIdentity().getDiscordId() + ">";
                String mentionFrom = "<@" + e.getFrom().getIdentity().getDiscordId() + ">";

                String message = String.format(
                        "\uD83E\uDEE6 O(a) %s chegou no seu ápice e \uD83C\uDF46\uD83D\uDCA6 gozou no %s.",
                        mentionOf,
                        mentionFrom

                );

                discordService.sendMessage(message, e.getIdChannel());
                e.setNotified(true);
                e.setStatus(EjaculateStatus.FINALIZADO);
            }
        }
        ejaculateRepository.saveAll(ejaculates);
    }

    @Transactional
    public void deleteEjaculates() {
        Set<Ejaculate> ejaculates = ejaculateRepository.findByNotified(true);
        ejaculateRepository.deleteAll(ejaculates);
    }
}
