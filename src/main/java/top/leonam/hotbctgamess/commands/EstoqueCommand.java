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
public class EstoqueCommand extends AbstractTrabalhoCommand {

    private final GameBalanceProperties.WorkItem balance;

    public EstoqueCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            UniversityRepository universityRepository,
            CacheService cacheService,
            GameBalanceProperties balanceProperties,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, universityRepository, cacheService, balanceProperties.getWork(), random);
        this.balance = balanceProperties.getWork().getEstoque();
    }

    @Override
    public String name() {
        return ".estoque";
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
                Trabalho: Estoque 📦
                Ganho: R$%.2f
                Total de turnos: %d
                """;
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalEstoque() == null) {
            job.setTotalEstoque(0L);
        }
        job.setTotalEstoque(job.getTotalEstoque() + 1);
        jobRepository.save(job);
        return job.getTotalEstoque();
    }
}
