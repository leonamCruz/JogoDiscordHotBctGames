package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.DailyStats;
import top.leonam.hotbctgamess.repository.DailyStatsRepository;
import top.leonam.hotbctgamess.repository.MessageRepository;

import java.awt.*;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final MessageRepository messageRepository;
    private final DailyStatsRepository dailyStatsRepository;

    @Transactional
    public void generateDailyStats(LocalDate date) {

        Instant start = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        BigInteger totalMessages = safe(messageRepository.countByTimestampBetween(start, end));
        BigInteger totalUsersActive = safe(messageRepository.countDistinctUsers(start, end));
        BigInteger totalCharacters = safe(messageRepository.sumCharacters(start, end));
        BigInteger totalMentions = safe(messageRepository.sumMentions(start, end));
        BigInteger totalLinks = safe(messageRepository.sumLinks(start, end));

        Long mostActiveChannelId = messageRepository
                .findMostActiveChannel(start, end, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        Integer peakHour = messageRepository.findPeakHour(start, end);

        DailyStats stats = new DailyStats();
        stats.setDate(date);
        stats.setTotalMessages(totalMessages);
        stats.setTotalUsersActive(totalUsersActive);
        stats.setTotalCharacters(totalCharacters);
        stats.setTotalMentions(totalMentions);
        stats.setTotalLinks(totalLinks);
        stats.setMostActiveChannelId(mostActiveChannelId);
        stats.setPeakHour(peakHour);

        dailyStatsRepository.save(stats);
    }

    private BigInteger safe(BigInteger value) {
        return value != null ? value : BigInteger.ZERO;
    }

    public DailyStats getDailyStats(LocalDate date) {
        return dailyStatsRepository.getByDate(date == null ? LocalDate.now() : date);
    }

    @Transactional
    @Cacheable(value = "dailyStats", key = "#date")
    public EmbedBuilder getDailyStatsEmbed(LocalDate date) {
        log.info("getDailyStatsEmbed date:{}", date);
        var embed = new EmbedBuilder();

        var stats = getDailyStats(date);

        if (stats == null) {
            embed.setTitle("Status da Mineração");

            embed.setTimestamp(Instant.now());
            embed.setColor(Color.RED);
            embed.setThumbnail("https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExcnU2ZWtzemRsZmRzYzY4NDRkaWRhdGZ6d3prOTBheXg1dWVnNW5iMyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/QbBz1DPfH5ivzFFwt2/giphy.gif");
            embed.setAuthor("HotBct Games");
            embed.setDescription("Calma Moreno, a vida não é um morango. Amanhã você volta para ter dados.");

            embed.setFooter("Aprendi com o Sávio Gameplay's");

            return embed;
        }

        embed.setTitle("Status da Mineração");
        embed.setTimestamp(Instant.now());
        embed.setColor(Color.GREEN);
        embed.setThumbnail("https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExcnU2ZWtzemRsZmRzYzY4NDRkaWRhdGZ6d3prOTBheXg1dWVnNW5iMyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/QbBz1DPfH5ivzFFwt2/giphy.gif");
        embed.setAuthor("HotBct Games");
        embed.setDescription(stats == null ? "Não temos dados seu pirento." :
                """
                        📅 **Data:** %s
                        
                        💬 **Mensagens totais:** %,d
                        👥 **Usuários ativos:** %,d
                        🔤 **Caracteres enviados:** %,d
                        
                        🔗 **Links enviados:** %,d
                        📣 **Menções feitas:** %,d
                        
                        ⏰ **Horário de pico:** %02dh
                        🔥 **Canal mais ativo:** <#%d>
                        """
                        .formatted(
                                stats.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                stats.getTotalMessages().longValue(),
                                stats.getTotalUsersActive().longValue(),
                                stats.getTotalCharacters().longValue(),
                                stats.getTotalLinks().longValue(),
                                stats.getTotalMentions().longValue(),
                                stats.getPeakHour() != null ? stats.getPeakHour() : 0,
                                stats.getMostActiveChannelId() != null ? stats.getMostActiveChannelId() : 0L
                        )
        );

        embed.setFooter("Aprendi com o Sávio Gameplay's");
        return embed;
    }
}
