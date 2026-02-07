package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Player;
import top.leonam.hotbctgamess.repository.EggRepository;

@Service
@Slf4j
@AllArgsConstructor
public class EggService {
    private final EggRepository eggRepository;
    @Transactional
    public void regenerateEggs() {
        eggRepository.updateMaxEjaculateForAll(3);
    }
    @Transactional
    public Integer getAmmountOfEjaculateRemaining(Player player) {
        log.info("EU RODEI AQUI");
        return eggRepository.returnRemainingQuantity(player);

    }
    @Transactional
    public void minusEjaculate(Player player) {
        eggRepository.updateRemainingQuantitySubtractOne(player);
    }
}
