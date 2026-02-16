package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.*;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class BetCommand extends AbstractCrimeCommand {

    public BetCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            PrisonRepository prisonRepository,
            UniversityRepository universityRepository,
            CacheService cacheService,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, prisonRepository, universityRepository, cacheService, random);
    }

    @Override
    public String name() {
        return ".bet";
    }

    @Override protected int ganhoMin() { return 500; }
    @Override protected int ganhoMax() { return 1500; }
    @Override protected int cooldown() { return 3; }
    @Override protected int levelMin() { return 3; }
    @Override protected Long minXp() { return 24L; }
    @Override protected int chancePrisao() { return 16; }

    @Override
    protected String descricaoTrabalho() {
        return """
                Crime: Bet clandestina 🎲
                Lucro: R$%.2f
                Total de crimes: %d
                """;
    }

    @Override
    protected String textoPrisao() {
        return "A casa caiu. Operacao de bet foi fechada.";
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalBet() == null) {
            job.setTotalBet(0L);
        }
        job.setTotalBet(job.getTotalBet() + 1);
        jobRepository.save(job);
        return job.getTotalBet();
    }
}
