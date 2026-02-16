package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.*;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class CcCommand extends AbstractCrimeCommand {

    private final GameBalanceProperties.CrimeItem balance;

    public CcCommand(
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
        this.balance = balanceProperties.getCrime().getCc();
    }

    @Override
    public String name() {
        return ".cc";
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
                Crime: CC 💳
                Lucro: R$%.2f
                Total de crimes: %d
                """;
    }

    @Override
    protected String textoPrisao() {
        return "🚔 🚨 A polícia rastreou a operação. Você foi em cana.";
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalCc() == null) {
            job.setTotalCc(0L);
        }
        job.setTotalCc(job.getTotalCc() + 1);
        jobRepository.save(job);
        return job.getTotalCc();
    }
}
