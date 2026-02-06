package top.leonam.hotbctgamess.scheduler;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.leonam.hotbctgamess.service.EjaculateService;

@Component
@AllArgsConstructor
public class EjaculateScheduler {

    private EjaculateService ejaculateService;

    @Scheduled(fixedRate = 15 * 1000)
    @Transactional
    public void checkEjaculates() {
        ejaculateService.verifyEjaculate();
    }
    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void deleteEjaculatesFinally() {
        ejaculateService.deleteEjaculates();
    }
}

