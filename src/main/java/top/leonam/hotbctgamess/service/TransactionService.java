package top.leonam.hotbctgamess.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Account;
import top.leonam.hotbctgamess.model.entity.Transaction;
import top.leonam.hotbctgamess.model.enums.TransactionStatus;
import top.leonam.hotbctgamess.model.enums.TypeTransaction;
import top.leonam.hotbctgamess.repository.AccountRepository;
import top.leonam.hotbctgamess.repository.TransactionRepository;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public boolean transfer(Account fromAccount, Account toAccount, BigDecimal amount, TypeTransaction typeTransaction) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            transactionRepository.saveAll(List.of(
                    Transaction.builder()
                            .originAccount(fromAccount)
                            .destinationAccount(toAccount)
                            .amount(amount.negate())
                            .typeTransaction(typeTransaction)
                            .status(TransactionStatus.FALHOU)
                            .build(),
                    Transaction.builder()
                            .originAccount(toAccount)
                            .destinationAccount(fromAccount)
                            .amount(amount)
                            .typeTransaction(typeTransaction)
                            .status(TransactionStatus.FALHOU)
                            .build()
            ));
            return false;
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        transactionRepository.saveAll(List.of(
                Transaction.builder()
                        .originAccount(fromAccount)
                        .destinationAccount(toAccount)
                        .amount(amount.negate())
                        .typeTransaction(typeTransaction)
                        .status(TransactionStatus.COMPLETO)
                        .build(),
                Transaction.builder()
                        .originAccount(toAccount)
                        .destinationAccount(fromAccount)
                        .amount(amount)
                        .typeTransaction(typeTransaction)
                        .status(TransactionStatus.COMPLETO)
                        .build()
        ));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        return true;
    }

    @Transactional
    public EmbedBuilder getExtract(Long id) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("📑 Extrato Bancário");
        embed.setColor(Color.CYAN);

        Set<Transaction> transactions = transactionRepository
                .findTop9ByOriginAccount_Player_Identity_DiscordIdOrderByCreatedAtDesc(id)
                .orElseThrow(() -> new RuntimeException("Transações não encontradas"));

        if (transactions.isEmpty()) {
            embed.setDescription("Você não realizou nenhuma transação recentemente.");
            return embed;
        }

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

        for (Transaction t : transactions) {
            boolean isNegative = t.getAmount().compareTo(BigDecimal.ZERO) < 0;
            String emoji = isNegative ? "🔴" : "🟢";
            String statusSymbol = t.getStatus() == TransactionStatus.FALHOU ? "⚠️ " : "";

            BigDecimal displayAmount = t.getAmount().abs();

            String fromName = t.getOriginAccount().getPlayer().getIdentity().getName();
            String toName = t.getDestinationAccount().getPlayer().getIdentity().getName();

            sb.append(String.format("%s %s**R$ %.2f** | %s\n", emoji, statusSymbol, displayAmount.doubleValue(), t.getTypeTransaction()));

            if (isNegative) {
                sb.append(String.format("└ *Para: %s*\n", toName));
            } else {
                sb.append(String.format("└ *De: %s*\n", fromName));
            }
            sb.append(String.format("└ *Data: %s*\n\n", t.getCreatedAt().format(formatter)));
        }

        embed.setDescription(sb.toString());
        embed.setFooter("As transações com 🟢 são entradas e 🔴 são saídas.");

        return embed;
    }

    @Transactional
    public void deposit(Account account, BigDecimal reward, TypeTransaction typeTransaction) {
        account.setBalance(account.getBalance().add(reward));
        Transaction transaction = Transaction.builder()
                .originAccount(account)
                .destinationAccount(account)
                .amount(reward)
                .typeTransaction(typeTransaction)
                .status(TransactionStatus.COMPLETO)
                .build();

        accountRepository.save(account);
        transactionRepository.save(transaction);
    }
}