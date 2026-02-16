package top.leonam.hotbctgamess.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.service.BtcMarketService;

import java.awt.*;
import java.math.BigDecimal;
import java.time.Instant;

@Service
public class CotacaoBtcCommand implements Command {

    private final BtcMarketService marketService;

    public CotacaoBtcCommand(BtcMarketService marketService) {
        this.marketService = marketService;
    }

    @Override
    public String name() {
        return ".cotacaobtc";
    }

    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        BigDecimal price = marketService.getCurrentPrice();
        return new EmbedBuilder()
                .setTitle("Cotacao do BTC 🪙")
                .setDescription("""
                        Preco atual: R$%.2f
                        """.formatted(price))
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.CYAN)
                .setFooter("HotBctsGames");
    }
}
