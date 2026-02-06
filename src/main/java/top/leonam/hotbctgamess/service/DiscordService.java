package top.leonam.hotbctgamess.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DiscordService {
    private JDA jda;

    public void sendMessage(String message, Long idChannel) {
        jda.getTextChannelById(idChannel).sendMessage(message).queue();
    }

}
