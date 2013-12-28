package com.clemble.casino.server.game.aspect.outcome;

import com.clemble.casino.game.GamePlayerContext;
import com.clemble.casino.game.GameSession;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.account.GamePlayerAccount;
import com.clemble.casino.game.outcome.GameOutcome;
import com.clemble.casino.game.outcome.PlayerWonOutcome;
import com.clemble.casino.payment.PaymentOperation;
import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.payment.money.Money;
import com.clemble.casino.payment.money.Operation;
import com.clemble.casino.server.game.aspect.BasicGameManagementAspect;
import com.clemble.casino.server.payment.PaymentTransactionServerService;

import java.util.Date;

/**
 * Created by mavarazy on 23/12/13.
 */
public class WonByPriceRuleAspect extends BasicGameManagementAspect{

    final private PaymentTransactionServerService transactionService;

    public WonByPriceRuleAspect(PaymentTransactionServerService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public <State extends GameState> void afterGame(GameSession<State> session) {
        GameOutcome outcome = session.getState().getOutcome();
        if (outcome instanceof PlayerWonOutcome) {
            String winnerId = ((PlayerWonOutcome) outcome).getWinner();
            Money price = session.getSpecification().getPrice();
            // Step 2. Generating payment transaction
            PaymentTransaction paymentTransaction = new PaymentTransaction()
                    .setTransactionKey(session.getSession().toPaymentTransactionKey())
                    .setTransactionDate(new Date());
            for (GamePlayerContext playerContext : session.getState().getContext().getPlayerContexts()) {
                GamePlayerAccount playerAccount = playerContext.getAccount();
                if (!playerAccount.getPlayer().equals(winnerId)) {
                    paymentTransaction
                        .addPaymentOperation(new PaymentOperation(playerAccount.getPlayer(), price, Operation.Credit))
                        .addPaymentOperation(new PaymentOperation(winnerId, price, Operation.Debit));
                }
            }
            // Step 3. Processing payment transaction
            transactionService.process(paymentTransaction);
        }
    }

}
