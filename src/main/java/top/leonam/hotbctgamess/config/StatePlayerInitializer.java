package top.leonam.hotbctgamess.config;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.leonam.hotbctgamess.service.PlayerService;

@Component
@AllArgsConstructor
public class StatePlayerInitializer implements CommandLineRunner {

    private final PlayerService playerService;
    private final JDA jda;

    @Override
    public void run(String... args) {
        long botId = jda.getSelfUser().getIdLong();

        playerService.registerStateIfAbsent(
                botId,
                "ESTADO"
        );
    }
}
