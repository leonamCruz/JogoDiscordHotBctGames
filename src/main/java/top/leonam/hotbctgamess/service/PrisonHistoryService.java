package top.leonam.hotbctgamess.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.PrisonHistory;
import top.leonam.hotbctgamess.repository.PrisonHistoryRepository;

@Service
@Slf4j
@AllArgsConstructor
public class PrisonHistoryService {
    private PrisonHistoryRepository prisonHistoryRepository;

    public void save(PrisonHistory prisonHistory){
        prisonHistoryRepository.save(prisonHistory);
    }
}
