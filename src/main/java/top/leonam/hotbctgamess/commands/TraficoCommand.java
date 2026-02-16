package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.repository.*;

import java.util.Random;

@Service
public class TraficoCommand extends AbstractCrimeCommand {

    private static final int GANHO_MIN = 2600;
    private static final int GANHO_MAX = 5500;
    private static final int CHANCE_PRISAO = 22;
    private static final int LEVEL_MIN = 5;
    private static final int COOLDOWN_SECONDS = 10;

    public TraficoCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            PrisonRepository prisonRepository,
            UniversityRepository universityRepository,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, prisonRepository,universityRepository, random);
    }

    @Override
    protected int ganhoMin() {
        return GANHO_MIN;
    }

    @Override
    protected int ganhoMax() {
        return GANHO_MAX;
    }

    @Override
    protected int chancePrisao() {
        return CHANCE_PRISAO;
    }

    @Override
    protected int levelMin() {
        return LEVEL_MIN;
    }

    @Override
    protected int cooldown() {
        return 3;
    }

    @Override
    protected String textoPrisao() {
        return "Você foi pego traficando 🚔! Fique preso por um tempo ou pague a fiança.";
    }

    @Override
    protected String descricaoTrabalho() {
        return """
                Crime: Trafico 💊
                Lucro: R$%.2f
                Total de crimes: %d
                """;
    }

    @Override
    protected Long minXp() {
        return 30L;
    }

    @Override
    public String name() {
        return ".trafico";
    }
}
