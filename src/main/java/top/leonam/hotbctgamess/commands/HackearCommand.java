package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.*;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class HackearCommand extends AbstractCrimeCommand {

    private final GameBalanceProperties.CrimeItem balance;

    public HackearCommand(
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
        this.balance = balanceProperties.getCrime().getHackear();
    }

    @Override
    public String name() {
        return ".hackear";
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
                Crime: Hackear 💻
                Lucro: R$%.2f
                Total de crimes: %d
                """;
    }

    @Override
    protected String textoPrisao() {
        return "O rastreio foi rapido. Hack falhou e voce foi preso.";
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalHackear() == null) {
            job.setTotalHackear(0L);
        }
        job.setTotalHackear(job.getTotalHackear() + 1);
        jobRepository.save(job);
        return job.getTotalHackear();
    }
}
