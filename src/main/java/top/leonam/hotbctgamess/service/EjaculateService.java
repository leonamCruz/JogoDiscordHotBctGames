package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Ejaculate;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.model.enums.EjaculateStatus;
import top.leonam.hotbctgamess.repository.EjaculateRepository;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class EjaculateService {
    private final EjaculateRepository ejaculateRepository;
    private final DiscordService discordService;

    @Transactional
    public EmbedBuilder ejaculateIn(Player playerOne, Player playerTwo, MessageReceivedEvent event) {
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

        EmbedBuilder embed = getEmbedBuilder(playerOne, playerTwo, minutes);

        embed.setFooter("Aguarde a conclusão do ato...", event.getAuthor().getEffectiveAvatarUrl());

        return embed;
    }

    private static @NonNull EmbedBuilder getEmbedBuilder(Player playerOne, Player playerTwo, int minutes) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(173, 216, 230));
        embed.setTitle("🍆💦 Preparando o Lançamento...");

        String mentionOne = "<@" + playerOne.getIdentity().getDiscordId() + ">";
        String mentionTwo = "<@" + playerTwo.getIdentity().getDiscordId() + ">";

        embed.setDescription(String.format(
                "%s iniciou o processo e o ápice em %s ocorrerá em breve!",
                mentionOne, mentionTwo
        ));

        embed.addField("⏱️ Tempo estimado", minutes + " minutos", true);
        embed.addField("🎯 Alvo", mentionTwo, true);
        return embed;
    }

    @Transactional
    public void verifyEjaculate() {
        Set<Ejaculate> ejaculates = ejaculateRepository.findByWhenWillItHappenBefore(LocalDateTime.now());

        for (Ejaculate e : ejaculates) {
            if (!e.isNotified()) {
                EmbedBuilder embedFinish = getEmbedBuilder(e);

                discordService.sendMessage(embedFinish, e.getIdChannel());

                e.setNotified(true);
                e.setStatus(EjaculateStatus.FINALIZADO);
            }
        }
        ejaculateRepository.saveAll(ejaculates);
    }

    private static @NonNull EmbedBuilder getEmbedBuilder(Ejaculate e) {
        String mentionOf = "<@" + e.getOf().getIdentity().getDiscordId() + ">";
        String mentionFrom = "<@" + e.getFrom().getIdentity().getDiscordId() + ">";

        EmbedBuilder embedFinish = new EmbedBuilder();
        embedFinish.setColor(Color.WHITE);
        embedFinish.setTitle("💦 ÁPICE ALCANÇADO!");
        embedFinish.setDescription(String.format(
                "O(a) %s chegou no seu limite e 🍆💦 **gozou** tudo no(a) %s!",
                mentionOf, mentionFrom
        ));
        embedFinish.setThumbnail("https://www.sex.com/pt/gifs/152149?utm_source=direct&utm_medium=shared-link-from-gif-viewer");
        return embedFinish;
    }

    @Transactional
    public void deleteEjaculates() {
        Set<Ejaculate> ejaculates = ejaculateRepository.findByNotified(true);
        ejaculateRepository.deleteAll(ejaculates);
    }
}