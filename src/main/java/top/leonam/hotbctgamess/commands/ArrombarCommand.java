package top.leonam.hotbctgamess.commands;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.enums.MilkShakes;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Random;

@Service
@AllArgsConstructor
public class ArrombarCommand implements Command {

    private Random random;

    @Override
    public String name() {
        return ".arrombar";
    }

    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {

        // valida menção
        if (event.getMessage().getMentions().getMembers().isEmpty()) {
            return new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Ai que delícia.")
                    .setDescription("Você precisa mencionar alguém. 🍩")
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setImage("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExdTdlb2VkYzgydXRsb3FuaHNvcjM4YnNkOWZvZzhwMm9zZ2VtNDJjbCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/12wiqgBM40LOzS/giphy.gif")
                    .setTimestamp(Instant.now())
                    .setFooter("HotBctsGames - Utilize .arrombar @<id>");
        }

        var alvo = event.getMessage().getMentions().getMembers().getFirst();

        // evita o show de narcisismo
        if (alvo.getIdLong() == event.getAuthor().getIdLong()) {
            return new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Ai que delícia.")
                    .setDescription("Isso aí não funciona assim.")
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setImage("https://tenor.com/pt-BR/view/fosca-flamejante-donut-man-gif-11989025")
                    .setTimestamp(Instant.now());
        }

        var autorNome = event.getMember() != null
                ? event.getMember().getEffectiveName()
                : event.getAuthor().getName();

        MilkShakes[] milkShakes = MilkShakes.values();
        MilkShakes sorteado = milkShakes[random.nextInt(milkShakes.length)];

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);
        eb.setAuthor(
                autorNome,
                event.getAuthor().getAvatarUrl(),
                event.getMember() != null ? event.getMember().getEffectiveAvatarUrl() : null
        );
        eb.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());
        eb.setImage("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExbWhnbnBqdTdxMGdqem03d3U4OTA2aXAxeWdsY2VzZXFwazlhZnY5ciZlcD12MV9naWZzX3NlYXJjaCZjdD1n/du4D0b0HWgxGg/giphy.gif");
        eb.setTitle(
                "Arrombamento liberado 🍩"
        );
        eb.setDescription("""
                Autor: %s
                Alvo: %s
                """.formatted(autorNome, alvo.getEffectiveName()));
        eb.setFooter(sorteado.getTextoFormatado());
        eb.setTimestamp(Instant.now());

        return eb;
    }
}
