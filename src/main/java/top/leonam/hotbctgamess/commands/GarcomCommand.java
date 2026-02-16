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
public class GarcomCommand extends AbstractTrabalhoCommand {

    public GarcomCommand(
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
        return ".garçom";
    }

    @Override
    protected Long minXp() {
        return 16L;
    }

    @Override
    protected int ganhoMin() {
        return 12;
    }

    @Override
    protected int ganhoMax() {
        return 24;
    }

    @Override
    protected int cooldown() {
        return 3;
    }

    @Override
    protected int levelMin() {
        return 1;
    }

    @Override
    protected String descricaoTrabalho() {
        return """
                Trabalho: Garcom 🍽️
                Ganho: R$%.2f
                Total de atendimentos: %d
                """;
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalGarcom() == null) {
            job.setTotalGarcom(0L);
        }
        job.setTotalGarcom(job.getTotalGarcom() + 1);
        jobRepository.save(job);
        return job.getTotalGarcom();
    }
}
