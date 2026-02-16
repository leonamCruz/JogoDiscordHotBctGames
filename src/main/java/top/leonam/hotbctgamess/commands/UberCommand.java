package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.config.GameBalanceProperties;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.JobRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;
import top.leonam.hotbctgamess.repository.UniversityRepository;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class UberCommand extends AbstractTrabalhoCommand {

    private final GameBalanceProperties.WorkItem balance;

    public UberCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            UniversityRepository universityRepository,
            CacheService cacheService,
            GameBalanceProperties balanceProperties,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, universityRepository, cacheService, balanceProperties.getWork(), random);
        this.balance = balanceProperties.getWork().getUber();
    }

    @Override
    public String name() {
        return ".uber";
    }

    @Override
    protected Long minXp() {
        return balance.getXp();
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
    protected int cooldown() {
        return balance.getCooldown();
    }

    @Override
    protected int levelMin() {
        return balance.getLevelMin();
    }

    @Override
    protected String descricaoTrabalho() {
        return """
                Trabalho: Uber 🚗
                Ganho: R$%.2f
                Total de corridas: %d
                """;
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalUber() == null) {
            job.setTotalUber(0L);
        }
        job.setTotalUber(job.getTotalUber() + 1);
        jobRepository.save(job);
        return job.getTotalUber();
    }
}
