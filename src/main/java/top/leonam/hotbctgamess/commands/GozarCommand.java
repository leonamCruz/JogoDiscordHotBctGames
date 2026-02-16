package top.leonam.hotbctgamess.commands;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
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
public class GozarCommand implements Command {

    private Random random;

    @Override
    public String name() {
        return ".gozar";
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
                    .setTimestamp(Instant.now())
                    .setFooter("HotBctsGames - Utilize .gozar @<id>");
        }

        if (event.getMessage().getMentions().getMembers().getFirst().getIdLong()
                == event.getAuthor().getIdLong()) {

            return new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Ai que delícia.")
                    .setDescription("Isso aí já é demais.")
                    .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                    .setImage("https://tenor.com/pt-BR/view/fosca-flamejante-donut-man-gif-11989025")
                    .setTimestamp(Instant.now());
        }

        var autorNome = event.getMember().getEffectiveName();

        Member alvo;
        alvo = event.getMessage().getMentions().getMembers().getFirst();

        if(event.getMessage().getMentions().getMembers().size() >= 2){
            alvo = event.getMessage().getMentions().getMembers().getLast();
        }

        MilkShakes[] milkShakes = MilkShakes.values();
        MilkShakes sorteado = milkShakes[random.nextInt(milkShakes.length)];

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.MAGENTA);
        eb.setAuthor(
                autorNome,
                event.getAuthor().getAvatarUrl(),
                event.getMember() != null ? event.getMember().getEffectiveAvatarUrl() : null
        );
        eb.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());
        eb.setImage("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExeWNvMTloNGV1ano4OXN0NzkxcnhteXNiN3FwZWZienA1ZHJkZGVxdCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/CpGsVKnfXBJBj5EjSq/giphy.gif");
        eb.setTitle("Gozada liberada 💦");
        eb.setDescription("""
                Autor: %s
                Alvo: %s
                """.formatted(autorNome, alvo.getEffectiveName()));
        eb.setFooter(sorteado.getTextoFormatado());
        eb.setTimestamp(Instant.now());

        return eb;
    }
}
