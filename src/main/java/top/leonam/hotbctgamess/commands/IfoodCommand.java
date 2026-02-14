package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.JobRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;

import java.util.Random;
@Service
public class IfoodCommand extends AbstractTrabalhoCommand {

    public IfoodCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository,random);
    }

    @Override
    public String name() {
        return "~ifood";
    }

    @Override
    protected Long minXp() {
        return 10L;
    }

    @Override
    protected int ganhoMin() {
        return 7;
    }

    @Override
    protected int ganhoMax() {
        return 10;
    }

    @Override
    protected int cooldown() {
        return 30;
    }

    @Override
    protected int levelMin() {
        return 0;
    }

    @Override
    protected String descricaoTrabalho() {
        return """
                Você trabalhou entregando iFood 🚲 e ganhou R$%.2f.
                Total de entregas: %d
                """;
    }
}
