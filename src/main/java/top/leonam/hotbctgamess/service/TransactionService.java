package top.leonam.hotbctgamess.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.leonam.hotbctgamess.model.entity.Account;
import top.leonam.hotbctgamess.model.entity.Transaction;
import top.leonam.hotbctgamess.model.enums.TransactionStatus;
import top.leonam.hotbctgamess.model.enums.TypeTransaction;
import top.leonam.hotbctgamess.repository.AccountRepository;
import top.leonam.hotbctgamess.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

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
    public String getExtract(Long id) {
        Set<Transaction> transactions = transactionRepository.findTop5ByOriginAccount_Player_Identity_DiscordIdOrderByCreatedAtDesc(id)
                .orElseThrow(() -> new RuntimeException("Transações não encontradas"));

        if (transactions.isEmpty()) return "Você não realizou nenhuma transação neste período.";

        StringBuilder sb = generateExtract("📑 **ÚLTIMAS TRANSAÇÕES**\n\n", transactions);

        return sb.toString();
    }

    @Transactional
    public String getExtract(Long id, LocalDateTime dateTime) {
        LocalDateTime start = dateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMonths(1);

        Set<Transaction> transactions = transactionRepository
                .findByOriginAccount_Player_Identity_DiscordIdAndCreatedAtBetweenOrderByCreatedAtDesc(id, start, end)
                .orElseThrow(() -> new RuntimeException("Nenhuma transação no período"));

        if (transactions.isEmpty()) return "Você não realizou nenhuma transação neste período.";

        StringBuilder sb = generateExtract(String.format("📑 **EXTRATO MENSAL (%02d/%d)**\n\n",
                dateTime.getMonthValue(), dateTime.getYear()), transactions);

        return sb.toString();
    }
    @Transactional
    public StringBuilder generateExtract(String title, Set<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);

        for (Transaction t : transactions) {
            BigDecimal displayAmount = t.getAmount().abs();

            String from = t.getAmount().compareTo(BigDecimal.ZERO) < 0 ? t.getOriginAccount().getPlayer().getIdentity().getName() : t.getDestinationAccount().getPlayer().getIdentity().getName();
            String to = t.getAmount().compareTo(BigDecimal.ZERO) < 0 ? t.getDestinationAccount().getPlayer().getIdentity().getName() : t.getOriginAccount().getPlayer().getIdentity().getName();

            sb.append(String.format("• R$ %.2f | %s | %s | De: %s | Para: %s \n",
                    displayAmount.doubleValue(),
                    t.getTypeTransaction(),
                    t.getStatus(),
                    from,
                    to
            ));
        }
        return sb;
    }

    @Transactional
    public void deposit(Account account, BigDecimal reward, TypeTransaction typeTransaction) {
        account.setBalance(account.getBalance().add(reward));
        Transaction transaction = Transaction.builder().originAccount(account).destinationAccount(account).amount(reward).typeTransaction(typeTransaction).status(TransactionStatus.COMPLETO).build();

        accountRepository.save(account);
        transactionRepository.save(transaction);
    }
}
