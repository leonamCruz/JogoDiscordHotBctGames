package top.leonam.hotbctgamess.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.leonam.hotbctgamess.service.StatsService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class StatsScheduler {
    private final StatsService statsService;
    @Scheduled(cron = "0 0 11 * * *")
    public void runStats() {
        statsService.generateDailyStats(LocalDate.now());
    }
}
