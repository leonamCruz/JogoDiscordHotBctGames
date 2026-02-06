package top.leonam.hotbctgamess.service.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ReturnCommands implements Command {
    private List<Command> allCommands;

    @Override
    public String name() {
        return "?comandos";
    }
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("📜 Guia de Comandos - HotBCT Games");
        embed.setColor(new Color(155, 89, 182));
        embed.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl());
        embed.setDescription("Olá! Aqui estão os comandos disponíveis:");

        embed.addField("⌨️ Comandos Disponíveis", getFormattedCommandList(), false);

        embed.addField("💡 Dica", "Use os comandos seguindo as instruções para progredir.", false);

        embed.setFooter("Solicitado por " + event.getAuthor().getName(),
                event.getAuthor().getEffectiveAvatarUrl());

        return embed;
    }
    public String getFormattedCommandList() {
        return allCommands.stream()
                .map(cmd -> "`" + cmd.name() + "`")
                .collect(Collectors.joining("  "));
    }
}
