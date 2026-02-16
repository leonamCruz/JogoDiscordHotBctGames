package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.*;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class SequestroCommand extends AbstractCrimeCommand {

    public SequestroCommand(
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
        return ".sequestro";
    }

    @Override protected int ganhoMin() { return 3500; }
    @Override protected int ganhoMax() { return 9000; }
    @Override protected int cooldown() { return 3; }
    @Override protected int levelMin() { return 6; }
    @Override protected Long minXp() { return 45L; }
    @Override protected int chancePrisao() { return 30; }

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
