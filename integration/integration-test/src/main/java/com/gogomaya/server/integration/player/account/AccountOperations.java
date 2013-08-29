package com.gogomaya.server.integration.player.account;

import java.util.List;

import com.gogomaya.money.MoneySource;
import com.gogomaya.payment.PaymentTransaction;
import com.gogomaya.payment.PlayerAccount;
import com.gogomaya.server.integration.player.Player;

public interface AccountOperations {

    public PlayerAccount getAccount(Player player);

    public PlayerAccount getAccount(Player player, long playerId);

    public List<PaymentTransaction> getTransactions(Player player);

    public List<PaymentTransaction> getTransactions(Player player, long playerId);

    public PaymentTransaction getTransaction(Player player, MoneySource moneySource, long transactionId);

    public PaymentTransaction getTransaction(Player player, long playerId, MoneySource moneySource, long transactionId);

    public PaymentTransaction getTransaction(Player player, String moneySource, long transactionId);

    public PaymentTransaction getTransaction(Player player, long playerId, String moneySource, long transactionId);

}
