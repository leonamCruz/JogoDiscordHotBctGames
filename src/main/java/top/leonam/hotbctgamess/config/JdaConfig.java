package top.leonam.hotbctgamess.config;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class JdaConfig {

    @Value("${token-bot-discord}")
    private String token;

    @Bean
    public JDA jda() throws Exception {
        if (token == null || token.isEmpty()) {
            log.error("O Token do Bot do Discord não foi localizado.");
            System.exit(-1);
        }

        return JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES
                )
                .build()
                .awaitReady();
    }
}
