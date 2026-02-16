package top.leonam.hotbctgamess.commands;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.interfaces.Command;
import top.leonam.hotbctgamess.model.entity.University;
import top.leonam.hotbctgamess.repository.UniversityRepository;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.util.Random;

@Service
@AllArgsConstructor
public class FaculdadeCommand implements Command {
    private UniversityRepository universityRepository;
    private Random random;
    private static final int COOLDOWN = 20 * 60;


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

        if (university.getConseguiu()) {
            embedBuilder.setDescription("""
                    Status: Formado ✅
                    Desde: %s
                    """.formatted(university.getQuandoConsegui().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            return embedBuilder;
        }

        if (university.getUltimaTentativa() == null) {
            university.setUltimaTentativa(LocalDateTime.now());
            universityRepository.save(university);

            embedBuilder.setDescription("""
                    Status: Em andamento 📚
                    Proxima tentativa: %d minutos
                    """.formatted(COOLDOWN / 60));

            return embedBuilder;
        }

        Duration duration = Duration.between(university.getUltimaTentativa(), LocalDateTime.now());
        if (duration.toSeconds() >= COOLDOWN) {

            boolean conseguiu = random.nextInt(100) < 75;
            university.setUltimaTentativa(LocalDateTime.now());
            if(conseguiu){
                university.setConseguiu(true);
                university.setQuandoConsegui(LocalDateTime.now());
            }
            universityRepository.save(university);

            embedBuilder.setDescription(conseguiu
                    ? """
                    Status: Formado ✅
                    Mensagem: Voce conseguiu
                    """
                    : """
                    Status: Reprovado ❌
                    Mensagem: Vai ter que tentar de novo
                    """
            );
            return embedBuilder;
        }

        long segundosFaltando = COOLDOWN - duration.toSeconds();
        long minutosFaltando = (segundosFaltando + 59) / 60;
        embedBuilder.setDescription("""
                Status: Aguarde ⏳
                Retorno em: %d minutos
                """.formatted(minutosFaltando));

        return embedBuilder;
    }
}
