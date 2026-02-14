package top.leonam.hotbctgamess.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;

import java.awt.*;
import java.time.LocalDateTime;

@Service
public class ArrombarCommand implements Command {
    @Override
    public String name() {
        return "~arrombar";
    }

    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ");
        if(message.length < 2) return new EmbedBuilder().setColor(Color.RED).setDescription("Você precisa arrombar alguém. \uD83C\uDF69").setTitle("Ai que delicia.").setThumbnail("https://tenor.com/pt-BR/view/fosca-flamejante-donut-man-gif-11989025");
        if(event.getMessage().getMentions().getMembers().getFirst().getIdLong() == event.getAuthor().getIdLong()) return new EmbedBuilder().setColor(Color.RED).setDescription("Você precisa arrombar alguém. \uD83C\uDF69").setTitle("Ai que delicia.").setThumbnail("https://tenor.com/pt-BR/view/fosca-flamejante-donut-man-gif-11989025");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);
        eb.setAuthor(event.getAuthor().getEffectiveName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getEffectiveAvatarUrl());
        eb.setThumbnail("https://tenor.com/pt-BR/view/donut-sticker-doughnut-donut-cake-junk-food-gif-26298208");
        eb.setTitle("O %s destruindo a rosquinha do %s, é muito cremoso.".formatted(event.getMessage().getAuthor().getName(), event.getMessage().getMentions().getMembers().getFirst().getUser().getName()));
        eb.setFooter("Sabor de Milk Shake de morango. ˗ˏˋ \uD83C\uDF53 ˎˊ˗");
        eb.setTimestamp(LocalDateTime.now());

        return eb;
        }
}
