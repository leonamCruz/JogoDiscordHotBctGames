package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.EconomyRepository;
import top.leonam.hotbctgamess.repository.JobRepository;
import top.leonam.hotbctgamess.repository.LevelRepository;
import top.leonam.hotbctgamess.repository.UniversityRepository;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class EstoqueCommand extends AbstractTrabalhoCommand {

    public EstoqueCommand(
            JobRepository jobRepository,
            EconomyRepository economyRepository,
            LevelRepository levelRepository,
            UniversityRepository universityRepository,
            CacheService cacheService,
            Random random
    ) {
        super(jobRepository, economyRepository, levelRepository, universityRepository, cacheService, random);
    }

    @Override
    public String name() {
        return ".estoque";
    }

    @Override
    protected Long minXp() {
        return 14L;
    }

    @Override
    protected int ganhoMin() {
        return 10;
    }

    @Override
    protected int ganhoMax() {
        return 20;
    }

    @Override
    protected int cooldown() {
        return 3;
    }

    @Override
    protected int levelMin() {
        return 0;
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
