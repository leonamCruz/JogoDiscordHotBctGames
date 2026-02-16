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
public class IfoodCommand extends AbstractTrabalhoCommand {

    private final GameBalanceProperties.WorkItem balance;

    public IfoodCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            UniversityRepository universityRepository,
            CacheService cacheService,
            GameBalanceProperties balanceProperties,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, universityRepository, cacheService, balanceProperties.getWork(), random);
        this.balance = balanceProperties.getWork().getIfood();
    }

    @Override
    public String name() {
        return ".ifood";
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
                Trabalho: Entregas iFood 🚲
                Ganho: R$%.2f
                Total de entregas: %d
                """;
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalIfood() == null) {
            job.setTotalIfood(0L);
        }
        job.setTotalIfood(job.getTotalIfood() + 1);
        jobRepository.save(job);
        return job.getTotalIfood();
    }
}
