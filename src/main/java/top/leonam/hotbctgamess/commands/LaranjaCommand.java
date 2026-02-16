package top.leonam.hotbctgamess.commands;

import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Job;
import top.leonam.hotbctgamess.repository.*;
import top.leonam.hotbctgamess.service.CacheService;

import java.util.Random;

@Service
public class LaranjaCommand extends AbstractCrimeCommand {

    public LaranjaCommand(
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
        return ".laranja";
    }

    @Override protected int ganhoMin() { return 300; }
    @Override protected int ganhoMax() { return 900; }
    @Override protected int cooldown() { return 3; }
    @Override protected int levelMin() { return 2; }
    @Override protected Long minXp() { return 22L; }
    @Override protected int chancePrisao() { return 12; }

    @Override
    protected String descricaoTrabalho() {
        return """
                Crime: Conta laranja 🥕
                Lucro: R$%.2f
                Total de crimes: %d
                """;
    }

    @Override
    protected String textoPrisao() {
        return "O banco desconfiou. Conta laranja rastreada.";
    }

    @Override
    protected long incrementarEObterTotal(Job job) {
        if (job.getTotalLaranja() == null) {
            job.setTotalLaranja(0L);
        }
        job.setTotalLaranja(job.getTotalLaranja() + 1);
        jobRepository.save(job);
        return job.getTotalLaranja();
    }
}
