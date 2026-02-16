package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.*;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class TraficoCommand extends AbstractCrimeCommand {

    private final GameBalanceProperties.CrimeItem balance;

    public TraficoCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            PrisonRepository prisonRepository,
            UniversityRepository universityRepository,
            CacheService cacheService,
            GameBalanceProperties balanceProperties,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, prisonRepository, universityRepository, cacheService, balanceProperties.getWork(), random);
        this.balance = balanceProperties.getCrime().getTrafico();
    }

    @Override
    protected int ganhoMin() {
        return balance.getGainMin();
    }

    @Override
    protected int ganhoMax() {
        return balance.getGainMax();
    }

    @Override
    protected int chancePrisao() {
        return balance.getPrisonChance();
    }

    @Override
    protected int levelMin() {
        return balance.getLevelMin();
    }

    @Override
    protected int cooldown() {
        return balance.getCooldown();
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
        return balance.getXp();
    }

    @Override
    public String name() {
        return ".trafico";
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalTrafico() == null) {
            job.setTotalTrafico(0L);
        }
        job.setTotalTrafico(job.getTotalTrafico() + 1);
        jobRepository.save(job);
        return job.getTotalTrafico();
    }
}
