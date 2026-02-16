package top.leonam.hotbctgamess.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.Instant;

@Service
public class AjudaCommand implements Command {

    @Override
    public String name() {
        return ".ajuda";
    }

    @Override
    @Cacheable(value = "help", key = "'help'")
    public EmbedBuilder execute(MessageReceivedEvent event) {
        String descricao = """
                Economia
                .energia - paga a energia diaria em kWh para minerar. ⚡
                .energia <packs> - compra kWh extra (fica mais caro com ASIC). 🔋
                .minerar - minera BTC consumindo energia. (Level 2) ⛏️
                .venderbtc <quantidade> - vende BTC no mercado. (Level 2) 🪙
                .cotacaobtc - mostra o preco do BTC. 📈
                .loja - lista produtos disponiveis. 🛒
                .comprar <id> - compra um produto da loja. 💸

                Trabalhos
                .ifood - entrega iFood e ganha dinheiro. (Level 0) 🚲
                .uber - roda de Uber e ganha dinheiro. (Level 3) 🚗
                .estoque - trabalha no estoque. (Level 0) 📦
                .garçom - trabalha como garcom. (Level 1) 🍽️
                .pedreiro - trabalha em obras. (Level 2) 🧱

                Crimes
                .cc - aplica golpes e ganha dinheiro. (Level 1) 💳
                .trafico - vende drogas e ganha dinheiro. (Level 5) 💊
                .roubar @user - rouba um jogador. (3x/dia) 🧤
                .laranja - abre conta laranja. (Level 2) 🥕
                .bet - opera bet clandestina. (Level 3) 🎲
                .hackear - hackeia sistemas. (Level 4) 💻
                .sequestro - alto risco, alto lucro. (Level 6) 🚨

                Social
                .arrombar @user - interacao com mencao. 🍩
                .gozar @user - interacao com mencao. 💦

                Progresso
                .faculdade - tenta se formar para bonus nos trabalhos. 🎓

                Util
                .inventario - mostra seus itens e bonus. 🎒
                .ranking - ranking geral com varios criterios. 🏆
                .perfil [@user] - mostra o perfil do jogador. 👤
                """;

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
