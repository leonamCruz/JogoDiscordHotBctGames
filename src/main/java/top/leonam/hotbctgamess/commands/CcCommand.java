package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.JobRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;
import top.leonam.hotbctgamess.repository.PrisonRepository;

import java.util.Random;

@Service
public class CcCommand extends AbstractCrimeCommand {

    public CcCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            PrisonRepository prisonRepository,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, prisonRepository, random);
    }

    @Override
    public String name() {
        return "~cc";
    }

    @Override
    protected int ganhoMin() {
        return 275;
    }

    @Override
    protected int ganhoMax() {
        return 1000;
    }

    @Override
    protected int cooldown() {
        return 10;
    }

    @Override
    protected int levelMin() {
        return 3;
    }

    @Override
    protected Long minXp() {
        return 25L;
    }

    @Override
    protected int chancePrisao() {
        return 10;
    }

    @Override
    protected String descricaoTrabalho() {
        return """
                Você aplicou um CC com sucesso 💳
                Lucro: R$%.2f | Total de crimes: %d
                """;
    }

    @Override
    protected String textoPrisao() {
        return "\uD83D\uDE94 \uD83D\uDEA8 A polícia rastreou a operação. Você foi em cana.";
    }
}
