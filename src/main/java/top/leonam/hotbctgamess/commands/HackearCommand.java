package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.*;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class HackearCommand extends AbstractCrimeCommand {

    public HackearCommand(
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
        return ".hackear";
    }

    @Override protected int ganhoMin() { return 800; }
    @Override protected int ganhoMax() { return 2200; }
    @Override protected int cooldown() { return 3; }
    @Override protected int levelMin() { return 4; }
    @Override protected Long minXp() { return 30L; }
    @Override protected int chancePrisao() { return 18; }

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
