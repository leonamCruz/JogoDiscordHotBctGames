package top.leonam.hotbctgamess.scheduler;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.leonam.hotbctgamess.service.EggService;

@Component
@AllArgsConstructor
public class EggScheduler {
    private EggService eggService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void regenerateEggs() {
        eggService.regenerateEggs();
    }
}
