package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.*;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class SequestroCommand extends AbstractCrimeCommand {

    private final GameBalanceProperties.CrimeItem balance;

    public SequestroCommand(
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
        this.balance = balanceProperties.getCrime().getSequestro();
    }

    @Override
    public String name() {
        return ".sequestro";
    }

    @Override protected int ganhoMin() { return balance.getGainMin(); }
    @Override protected int ganhoMax() { return balance.getGainMax(); }
    @Override protected int cooldown() { return balance.getCooldown(); }
    @Override protected int levelMin() { return balance.getLevelMin(); }
    @Override protected Long minXp() { return balance.getXp(); }
    @Override protected int chancePrisao() { return balance.getPrisonChance(); }

    @Override
    protected String descricaoTrabalho() {
        return """
                Crime: Sequestro 🚨
                Lucro: R$%.2f
                Total de crimes: %d
                """;
    }

    @Override
    protected String textoPrisao() {
        return "A policia caiu matando. Deu ruim no sequestro.";
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalSequestro() == null) {
            job.setTotalSequestro(0L);
        }
        job.setTotalSequestro(job.getTotalSequestro() + 1);
        jobRepository.save(job);
        return job.getTotalSequestro();
    }
}
