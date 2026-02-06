package top.leonam.hotbctgamess.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Crime;
import top.leonam.hotbctgamess.model.entity.CrimeHistory;
import top.leonam.hotbctgamess.repository.CrimeHistoryRepository;

@Service
@Slf4j
@AllArgsConstructor
public class CrimeHistoryService {
    private CrimeHistoryRepository crimeHistoryRepository;

    public void save(CrimeHistory crimeHistory){
        crimeHistoryRepository.save(crimeHistory);
    }

    public CrimeHistory getLastCrime(long idDiscord) {
        return crimeHistoryRepository.findFirstByPlayer_Identity_DiscordIdOrderByAttemptedAtDesc(idDiscord).orElseThrow();

    }
}
