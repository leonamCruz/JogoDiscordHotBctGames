package top.leonam.hotbctgamess.service.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Crime;
import top.leonam.hotbctgamess.service.CrimeService;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class ReturnCrimesCommand implements Command {

    private final CrimeService crimeService;

    @Override
    public String name() {
        return "?crimes";
    }

    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        List<Crime> crimes = crimeService.getAllCrimes();

        if (crimes.isEmpty()) {
            embed.setColor(Color.ORANGE);
            embed.setTitle("🚨 Sistema de Crimes");
            embed.setDescription("Nenhum crime disponível no momento. O sistema está mais honesto do que deveria.");
            return embed;
        }

        embed.setTitle("🕵️ Lista de Crimes Disponíveis");
        embed.setColor(Color.DARK_GRAY);
        embed.setThumbnail("https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExNWJndzg3NGFhbW55eGRpdWtlYXl5eGJjNmxwaTJ4bXdxZzZtaG56MSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/HL6PWuEFPb6TVj13Gs/giphy.gif");

        StringBuilder sb = new StringBuilder();

        for (Crime crime : crimes) {
            sb.append("### 🔪 ").append(crime.getName().toUpperCase()).append("\n")
                    .append("> ").append(crime.getDescription()).append("\n")
                    .append(String.format("🔓 **Nível:** `%d` | ⭐ **XP:** `%d`\n", crime.getMinLevel(), crime.getXp()))
                    .append(String.format("💰 **Recompensa:** `R$ %s - %s`\n", format(crime.getMinReward()), format(crime.getMaxReward())))
                    .append(String.format("🚓 **Risco:** `%d%%` | ⏱️ **Pena:** `%ds`\n\n", crime.getPoliceRisk(), crime.getCooldownSeconds()));
        }

        embed.setDescription(sb.toString());
        embed.setFooter("Use ?cometer <nome> para executar", event.getJDA().getSelfUser().getAvatarUrl());

        return embed;
    }

    private String format(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }
}