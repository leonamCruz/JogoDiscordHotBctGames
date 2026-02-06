package top.leonam.hotbctgamess.service.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Crime;
import top.leonam.hotbctgamess.service.CrimeService;

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
    public String execute(MessageReceivedEvent event) {
        List<Crime> crimes = crimeService.getAllCrimes();

        if (crimes.isEmpty()) return "🚨 **Nenhum crime disponível no momento.**\nO sistema está mais honesto do que deveria.";

        StringBuilder sb = new StringBuilder();
        sb.append("🕵️ **LISTA DE CRIMES DISPONÍVEIS** 🕵️\n\n");

        for (Crime crime : crimes) {
            sb.append("🔪 **").append(crime.getName()).append("**\n")
                    .append("📄 ").append(crime.getDescription()).append("\n")
                    .append("🎚️ Nível mínimo: ").append(crime.getMinLevel()).append("\n")
                    .append("⭐ XP: ").append(crime.getXp()).append("\n")
                    .append("💰 Recompensa: ")
                    .append(format(crime.getMinReward()))
                    .append(" - ")
                    .append(format(crime.getMaxReward()))
                    .append("\n")
                    .append("⏱️ Tempo de Prisão: ").append(crime.getCooldownSeconds()).append("s\n")
                    .append("🎯 Sucesso: ").append(crime.getSuccessChance()).append("%\n")
                    .append("🚓 Risco policial: ").append(crime.getPoliceRisk()).append("%\n\n");
        }

        return sb.toString();
    }

    private String format(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }
}
