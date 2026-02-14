package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Identity;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.repository.IdentityRepository;
import top.leonam.hotbctgamess.repository.PlayerRepository;

@Service
@RequiredArgsConstructor
public class BdService {
    private final IdentityRepository identityRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public void saveIdentityIfNotExists(MessageReceivedEvent event) {
        String name = event.getAuthor().getName();
        Long discordId = event.getAuthor().getIdLong();

        Identity identity = identityRepository
                .findByDiscordId(discordId)
                .orElseGet(() -> identityRepository.saveAndFlush(new Identity(name, discordId)));

        playerRepository.findByIdentity_DiscordId(discordId)
                .orElseGet(() -> playerRepository.saveAndFlush(new Player(identity)));
    }

}
