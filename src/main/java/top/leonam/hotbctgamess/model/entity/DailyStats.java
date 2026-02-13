package top.leonam.hotbctgamess.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Table
@Data
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    private BigInteger totalMessages;

    private BigInteger totalUsersActive;

    private BigInteger totalCharacters;

    private BigInteger totalMentions;

    private BigInteger totalLinks;

    private Integer peakHour;

    private Long mostActiveChannelId;

}

