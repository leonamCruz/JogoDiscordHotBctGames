package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.exceptions.UserNotFound;
import top.leonam.hotbctgamess.model.entity.Account;
import top.leonam.hotbctgamess.model.enums.TypeTransaction;
import top.leonam.hotbctgamess.repository.AccountRepository;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private AccountRepository accountRepository;

    public String walletStats(MessageReceivedEvent event){
        var idDiscord = event.getAuthor().getIdLong();

        Account account = accountRepository
                .findByPlayer_Identity_DiscordId(idDiscord)
                .orElseThrow(() -> {
                    log.info("Discord ID: {}", idDiscord);
                    log.info("O comando foi: {}", event.getMessage());
                    return new UserNotFound("Usuário não foi localizado, porém já deveria ter sido criado automaticamente.");
                });


        return """
            🏦 **Banco do Jogador**
      
            💰 Saldo: R$ %.2f

            """.formatted(account.getBalance());
    }

    public Account getAccountByDiscordId(Long discordId){
        return accountRepository
                .findByPlayer_Identity_DiscordId(discordId)
                .orElseThrow(() -> {
                    log.info("Discord ID: {}", discordId);
                    return new UserNotFound("Usuário não foi localizado, porém já deveria ter sido criado automaticamente.");
                });
    }

}
