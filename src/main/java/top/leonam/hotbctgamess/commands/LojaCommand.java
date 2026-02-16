package top.leonam.hotbctgamess.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.enums.StoreProduct;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class LojaCommand implements Command {

    @Override
    public String name() {
        return ".loja";
    }

    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        String list = Arrays.stream(StoreProduct.values())
                .map(StoreProduct::toDisplayLine)
                .collect(Collectors.joining("\n\n"));

        return new EmbedBuilder()
                .setTitle("Loja de Produtos")
                .setDescription(list + "\n\nAcao: use .comprar <id> para comprar.")
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.CYAN)
                .setFooter("HotBctsGames");
    }
}
