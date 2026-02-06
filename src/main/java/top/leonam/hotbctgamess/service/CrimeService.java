package top.leonam.hotbctgamess.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Crime;
import top.leonam.hotbctgamess.repository.CrimeRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CrimeService {
    private CrimeRepository crimeRepository;

    @Cacheable(cacheNames = "crimeByName", key = "#name")
    public Crime getCrimeByName(String name) {
        return crimeRepository.findByName(name).orElseThrow();
    }

    @Cacheable(cacheNames = "crimes")
    public List<Crime> getAllCrimes() {
        return crimeRepository.findAll();
    }
}
