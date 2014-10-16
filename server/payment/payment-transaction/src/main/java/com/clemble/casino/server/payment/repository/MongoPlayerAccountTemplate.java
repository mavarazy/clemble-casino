package com.clemble.casino.server.payment.repository;

import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import com.clemble.casino.payment.PaymentOperation;
import com.clemble.casino.payment.PendingOperation;
import com.clemble.casino.payment.PendingTransaction;
import com.clemble.casino.payment.PlayerAccount;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Collections;

/**
 * Created by mavarazy on 15/10/14.
 */
public class MongoPlayerAccountTemplate implements PlayerAccountTemplate {

    final private PlayerAccountRepository accountRepository;
    final private PendingTransactionRepository pendingTransactionRepository;

    public MongoPlayerAccountTemplate(
        PlayerAccountRepository accountRepository,
        PendingTransactionRepository pendingTransactionRepository) {
        this.accountRepository = accountRepository;
        this.pendingTransactionRepository = pendingTransactionRepository;
    }

    @Override
    public PlayerAccount findOne(String player) {
        return accountRepository.findOne(player);
    }

    @Override
    public void debit(String player, Money amount) {
        tryDebit(player, amount);
    }

    private void tryDebit(String player, Money amount) {
        try {
            PlayerAccount account = accountRepository.findOne(player);
            Money debit = account.getMoney(amount.getCurrency());
            if (debit == null) {
                account.getMoney().put(amount.getCurrency(), amount);
            } else {
                account.getMoney().put(amount.getCurrency(), debit.add(amount));
            }
            accountRepository.save(account);
        } catch (OptimisticLockingFailureException e) {
            // TODO This is dangerous approach to this problem
            tryDebit(player, amount);
        } catch (NullPointerException e) {
            // TODO this leaves a control breach for random PaymentAccount creation
            tryCreate(player);
            tryDebit(player, amount);
        }
    }

    private void tryCreate(String player) {
        try {
            // TODO this leaves a control breach for random PaymentAccount creation
            // Step 1. Creating new account
            PlayerAccount newAccount = new PlayerAccount(player, Collections.<Currency, Money>emptyMap(), Collections.<PendingOperation>emptyList(), null);
            // Step 2. Adding new account to repository
            accountRepository.save(newAccount);
        } catch (Throwable throwable) {

        }
    }

    @Override
    public void credit(String player, Money amount) {
        debit(player, amount.negate());
    }

    @Override
    public PendingTransaction freeze(PendingTransaction pendingTransaction) {
        for (PaymentOperation operation: pendingTransaction.getOperations()) {
            tryFreezingAccount(pendingTransaction.getTransactionKey(), operation);
            tryAddingToPending(pendingTransaction.getTransactionKey(), operation);
        }
        return pendingTransactionRepository.findOne(pendingTransaction.getTransactionKey());
    }

    private PendingTransaction tryAddingToPending(String transactionKey, PaymentOperation operation) {
        try {
            // Step 1. Creating or reading PendingTransaction
            PendingTransaction transaction = pendingTransactionRepository.findOne(transactionKey);
            if (transaction == null) {
                pendingTransactionRepository.save(new PendingTransaction(transactionKey, Collections.emptyList(), null));
                transaction = pendingTransactionRepository.findOne(transactionKey);
            }
            // Step 2. Updating PendingTransaction
            transaction.getOperations().add(operation);
            // Step 3. Saving updated PendingTransaction
            return pendingTransactionRepository.save(transaction);
        } catch (OptimisticLockingFailureException e) {
            // TODO This is dangerous approach to this problem
            return tryAddingToPending(transactionKey, operation);
        }
    }

    private PendingOperation tryFreezingAccount(String transactionKey, PaymentOperation operation) {
        try {
            Money amount = operation.getAmount();
            PlayerAccount account = accountRepository.findOne(operation.getPlayer());
            Money playerAmount = account.getMoney(amount.getCurrency());
            if (playerAmount == null) {
                account.getMoney().put(amount.getCurrency(), amount.negate());
            } else {
                account.getMoney().put(amount.getCurrency(), playerAmount.subtract(amount));
            }
            PendingOperation pendingOperation = PendingOperation.fromOperation(transactionKey, operation);
            account.getPendingOperations().add(pendingOperation);
            accountRepository.save(account);
            return pendingOperation;
        } catch (OptimisticLockingFailureException e) {
            // TODO This is dangerous approach to this problem
            return tryFreezingAccount(transactionKey, operation);
        }
    }

}
