package top.leonam.hotbctgamess.service.commands;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.service.AccountService;

@Service
@AllArgsConstructor
public class WalletCommand implements Command {
    private AccountService accountService;
    @Override
    public String name() {
        return "?carteira";
    }

    @Transactional
    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        return accountService.walletStats(event);
    }
}
