package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.repository.*;

import java.util.Random;

@Service
public class CcCommand extends AbstractCrimeCommand {

    public CcCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            PrisonRepository prisonRepository,
            UniversityRepository universityRepository,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, prisonRepository, universityRepository, random);
    }

    @Override
    public String name() {
        return ".cc";
    }

    @Override protected int ganhoMin() { return 150; }
    @Override protected int ganhoMax() { return 1000; }
    @Override protected int cooldown() { return 3; }
    @Override protected int levelMin() { return 1; }
    @Override protected Long minXp() { return 25L; }
    @Override protected int chancePrisao() { return 10; }

    @Override
    protected String descricaoTrabalho() {
        return """
                Crime: CC 💳
                Lucro: R$%.2f
                Total de crimes: %d
                """;
    }

    @Override
    protected String textoPrisao() {
        return "🚔 🚨 A polícia rastreou a operação. Você foi em cana.";
    }
}
