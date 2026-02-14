package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.JobRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;

import java.util.Random;

@Service
public class UberCommand extends AbstractTrabalhoCommand {

    public UberCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, random);
    }

    @Override
    public String name() {
        return "~uber";
    }

    @Override
    protected Long minXp() {
        return 20L;
    }

    @Override
    protected int ganhoMin() {
        return 12;
    }

    @Override
    protected int ganhoMax() {
        return 20;
    }

    @Override
    protected int cooldown() {
        return 45;
    }

    @Override
    protected int levelMin() {
        return 3;
    }

    @Override
    protected String descricaoTrabalho() {
        return """
                Você rodou como Uber 🚗 e ganhou R$%.2f.
                Total de corridas: %d
                """;
    }
}
