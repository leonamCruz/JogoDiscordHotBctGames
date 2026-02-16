package top.leonam.hotbctgamess.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.Economy;
import top.leonam.hotbctgamess.model.entity.University;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.UniversityRepository;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.util.Random;

@Service
public class FaculdadeCommand implements Command {
    private final UniversityRepository universityRepository;
    private final EconomyRepository economyRepository;
    private final Random random;
    private final GameBalanceProperties.Faculdade balance;

    public FaculdadeCommand(
            UniversityRepository universityRepository,
            EconomyRepository economyRepository,
            Random random,
            GameBalanceProperties balanceProperties
    ) {
        this.universityRepository = universityRepository;
        this.economyRepository = economyRepository;
        this.random = random;
        this.balance = balanceProperties.getFaculdade();
    }


    @Override
    public String name() {
        return ".faculdade";
    }

    @Override
    public EmbedBuilder execute(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setTitle("Faculdade\uD83C\uDF93");
        embedBuilder.setColor(Color.BLUE);
        embedBuilder.setFooter("HotBctsGames");
        embedBuilder.setThumbnail(event.getAuthor().getEffectiveAvatarUrl());
        embedBuilder.setImage("https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExMmxtZW4xd3cweDI0bXNjcWY0YWQ2cjUzaWs0ZzU3YjhmanVuczhnbSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/2UoGvl2KsT8xRYDe6o/giphy.gif");

        Long discordId = event.getAuthor().getIdLong();
        University university = universityRepository.findByPlayer_Identity_DiscordId(discordId);
        Economy economy = economyRepository.findByPlayer_Identity_DiscordId(discordId);

        if (university.getConseguiu()) {
            embedBuilder.setDescription("""
                    Status: Formado ✅
                    Desde: %s
                    """.formatted(university.getQuandoConsegui().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            return embedBuilder;
        }

        if (university.getUltimaTentativa() == null) {
            if (!temDinheiro(economy)) {
                embedBuilder.setDescription("""
                        Status: Sem dinheiro ❌
                        Valor da faculdade: R$%.2f
                        Saldo: R$%.2f
                        """.formatted(balance.getPrice(), economy.getMoney()));
                return embedBuilder;
            }
            cobrarFaculdade(economy);
            university.setUltimaTentativa(LocalDateTime.now());
            universityRepository.save(university);

            embedBuilder.setDescription("""
                    Status: Em andamento 📚
                    Proxima tentativa: %d minutos
                    Valor pago: R$%.2f
                    """.formatted(balance.getCooldownSeconds() / 60, balance.getPrice()));

            return embedBuilder;
        }

        Duration duration = Duration.between(university.getUltimaTentativa(), LocalDateTime.now());
        if (duration.toSeconds() >= balance.getCooldownSeconds()) {

            boolean conseguiu = random.nextDouble() < balance.getSuccessChance();
            if (!temDinheiro(economy)) {
                embedBuilder.setDescription("""
                        Status: Sem dinheiro ❌
                        Valor da faculdade: R$%.2f
                        Saldo: R$%.2f
                        """.formatted(balance.getPrice(), economy.getMoney()));
                return embedBuilder;
            }
            cobrarFaculdade(economy);
            university.setUltimaTentativa(LocalDateTime.now());
            university.setUltimoResultadoSucesso(conseguiu);
            university.setUltimoResultadoEm(LocalDateTime.now());
            if(conseguiu){
                university.setConseguiu(true);
                university.setQuandoConsegui(LocalDateTime.now());
            }
            universityRepository.save(university);

            embedBuilder.setDescription(conseguiu
                    ? """
                    Status: Formado ✅
                    Mensagem: Voce conseguiu
                    Valor pago: R$%.2f
                    """
                    : """
                    Status: Reprovado ❌
                    Mensagem: Vai ter que tentar de novo
                    Valor pago: R$%.2f
                    """
            .formatted(balance.getPrice()));
            return embedBuilder;
        }

        long segundosFaltando = balance.getCooldownSeconds() - duration.toSeconds();
        long minutosFaltando = (segundosFaltando + 59) / 60;
        String ultimoResultado = formatarUltimoResultado(university);
        embedBuilder.setDescription("""
                Status: Aguarde ⏳
                Retorno em: %d minutos
                %s
                """.formatted(minutosFaltando, ultimoResultado));

        return embedBuilder;
    }

    private boolean temDinheiro(Economy economy) {
        return economy != null
                && economy.getMoney() != null
                && economy.getMoney().compareTo(balance.getPrice()) >= 0;
    }

    private void cobrarFaculdade(Economy economy) {
        economy.setMoney(economy.getMoney().subtract(balance.getPrice()));
        economyRepository.save(economy);
    }

    private String formatarUltimoResultado(University university) {
        if (university.getUltimoResultadoSucesso() == null || university.getUltimoResultadoEm() == null) {
            return "Ultimo resultado: Nenhum";
        }
        String status = university.getUltimoResultadoSucesso() ? "Aprovado ✅" : "Reprovado ❌";
        String data = university.getUltimoResultadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return "Ultimo resultado: %s (%s)".formatted(status, data);
    }
}
