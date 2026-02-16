package top.leonam.hotbctgamess.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.interfaces.Command;

import java.awt.*;
import java.time.Instant;

@Service
public class AjudaCommand implements Command {

    private final GameBalanceProperties balance;

    public AjudaCommand(GameBalanceProperties balance) {
        this.balance = balance;
    }

    @Override
    public String name() {
        return ".ajuda";
    }

    @Override
    @Cacheable(value = "help", key = "'help'")
    public EmbedBuilder execute(MessageReceivedEvent event) {
        GameBalanceProperties.Work work = balance.getWork();
        GameBalanceProperties.Crime crime = balance.getCrime();
        String descricao = """
                Economia
                .energia - paga a energia diaria em kWh para minerar. ⚡
                .energia <packs> - compra kWh extra (fica mais caro com ASIC). 🔋
                .minerar - minera BTC consumindo energia. (Level %d) ⛏️
                .venderbtc <quantidade> - vende BTC no mercado. (Level %d) 🪙
                .cotacaobtc - mostra o preco do BTC. 📈
                .loja - lista produtos disponiveis. 🛒
                .comprar <id> - compra um produto da loja. 💸

                Trabalhos
                .ifood - entrega iFood e ganha dinheiro. (Level %d) 🚲
                .uber - roda de Uber e ganha dinheiro. (Level %d) 🚗
                .estoque - trabalha no estoque. (Level %d) 📦
                .garçom - trabalha como garcom. (Level %d) 🍽️
                .pedreiro - trabalha em obras. (Level %d) 🧱

                Crimes
                .cc - aplica golpes e ganha dinheiro. (Level %d) 💳
                .trafico - vende drogas e ganha dinheiro. (Level %d) 💊
                .roubar @user - rouba um jogador. (%dx/dia) 🧤
                .laranja - abre conta laranja. (Level %d) 🥕
                .bet - opera bet clandestina. (Level %d) 🎲
                .hackear - hackeia sistemas. (Level %d) 💻
                .sequestro - alto risco, alto lucro. (Level %d) 🚨

                Social
                .arrombar @user - interacao com mencao. 🍩
                .gozar @user - interacao com mencao. 💦

                Progresso
                .faculdade - tenta se formar (R$%.2f por tentativa). 🎓

                Util
                .inventario - mostra seus itens e bonus. 🎒
                .ranking - ranking geral com varios criterios. 🏆
                .perfil [@user] - mostra o perfil do jogador. 👤
                """.formatted(
                balance.getMining().getLevelMin(),
                balance.getBtc().getSellLevelMin(),
                work.getIfood().getLevelMin(),
                work.getUber().getLevelMin(),
                work.getEstoque().getLevelMin(),
                work.getGarcom().getLevelMin(),
                work.getPedreiro().getLevelMin(),
                crime.getCc().getLevelMin(),
                crime.getTrafico().getLevelMin(),
                balance.getRoubo().getDailyLimit(),
                crime.getLaranja().getLevelMin(),
                crime.getBet().getLevelMin(),
                crime.getHackear().getLevelMin(),
                crime.getSequestro().getLevelMin(),
                balance.getFaculdade().getPrice()
        );

        return new EmbedBuilder()
                .setTitle("Ajuda - Comandos")
                .setDescription(descricao)
                .setAuthor(event.getAuthor().getEffectiveName())
                .setThumbnail(event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Color.CYAN)
                .setFooter("HotBctsGames");
    }
}
