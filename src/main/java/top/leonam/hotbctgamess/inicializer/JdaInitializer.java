package top.leonam.hotbctgamess.inicializer;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Component;
import top.leonam.hotbctgamess.listener.JdaListener;

@Component
@AllArgsConstructor

public class JdaInitializer {

    private final JDA jda;
    private final JdaListener listener;

    @PostConstruct
    public void init() {
        jda.addEventListener(listener);
    }
}