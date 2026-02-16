package top.leonam.hotbctgamess.dto;

import java.math.BigDecimal;

public record RankingRow(
        String name,
        Long discordId,
        BigDecimal money,
        BigDecimal btc,
        Long totalJobs,
        Long totalCrimes,
        Long level,
        Long products,
        Long totalIfood,
        Long totalUber,
        Long totalEstoque,
        Long totalGarcom,
        Long totalPedreiro,
        Long totalCc,
        Long totalTrafico,
        Long totalSequestro,
        Long totalHackear,
        Long totalLaranja,
        Long totalBet,
        Long totalRoubar
) {
}
