package top.leonam.hotbctgamess.seed;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.leonam.hotbctgamess.model.entity.Crime;
import top.leonam.hotbctgamess.repository.CrimeRepository;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class CrimeDataLoader implements CommandLineRunner {

    private final CrimeRepository crimeRepository;

    @Override
    @Transactional
    public void run(String... args) {

        saveIfNotExists(
                Crime.builder()
                        .name("batercarteira")
                        .description("Roubar carteiras na rua.")
                        .minLevel(0)
                        .xp(100)
                        .minReward(BigDecimal.valueOf(10))
                        .maxReward(BigDecimal.valueOf(50))
                        .cooldownSeconds(30)
                        .successChance(90)
                        .policeRisk(5)
                        .build()
        );

        saveIfNotExists(
                Crime.builder()
                        .name("cc")
                        .description("Clonar cartão de idosos em sites falsos.")
                        .minLevel(2)
                        .xp(150)
                        .minReward(BigDecimal.valueOf(30))
                        .maxReward(BigDecimal.valueOf(250))
                        .cooldownSeconds(45)
                        .successChance(85)
                        .policeRisk(8)
                        .build()
        );

        saveIfNotExists(
                Crime.builder()
                        .name("dropsemshipping")
                        .description("Vende produtos na internet sem os entregar.")
                        .minLevel(3)
                        .xp(250)
                        .minReward(BigDecimal.valueOf(60))
                        .maxReward(BigDecimal.valueOf(380))
                        .cooldownSeconds(60)
                        .successChance(80)
                        .policeRisk(12)
                        .build()
        );

        saveIfNotExists(
                Crime.builder()
                        .name("golpepix")
                        .description("Engana a vítima para fazer transferências via PIX.")
                        .minLevel(2)
                        .xp(180)
                        .minReward(BigDecimal.valueOf(50))
                        .maxReward(BigDecimal.valueOf(300))
                        .cooldownSeconds(50)
                        .successChance(75)
                        .policeRisk(15)
                        .build()
        );

        saveIfNotExists(
                Crime.builder()
                        .name("fraudeemprestimo")
                        .description("Usa dados vazados para solicitar empréstimos.")
                        .minLevel(3)
                        .xp(400)
                        .minReward(BigDecimal.valueOf(500))
                        .maxReward(BigDecimal.valueOf(2_000))
                        .cooldownSeconds(90)
                        .successChance(65)
                        .policeRisk(20)
                        .build()
        );

        saveIfNotExists(
                Crime.builder()
                        .name("sequestro")
                        .description("Tira a liberdade das Pessoas.")
                        .minLevel(4)
                        .xp(2_000)
                        .minReward(BigDecimal.valueOf(10_000))
                        .maxReward(BigDecimal.valueOf(50_000))
                        .cooldownSeconds(60 * 12)
                        .successChance(70)
                        .policeRisk(18)
                        .build()
        );

        saveIfNotExists(
                Crime.builder()
                        .name("ransomware")
                        .description("Sequestra dados de empresas e cobra resgate.")
                        .minLevel(5)
                        .xp(3_000)
                        .minReward(BigDecimal.valueOf(15_000))
                        .maxReward(BigDecimal.valueOf(80_000))
                        .cooldownSeconds(60 * 20)
                        .successChance(56)
                        .policeRisk(25)
                        .build()
        );
    }

    private void saveIfNotExists(Crime crime) {
        crimeRepository.findByName(crime.getName())
                .ifPresentOrElse(
                        existing -> {},
                        () -> crimeRepository.save(crime)
                );
    }
}
