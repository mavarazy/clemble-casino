package com.clemble.casino.server.payment.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.clemble.casino.payment.service.PaymentTransactionServiceContract;
import com.clemble.casino.server.ExternalController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.server.payment.repository.PaymentTransactionRepository;
import com.clemble.casino.web.mapping.WebMapping;
import static com.clemble.casino.payment.PaymentWebMapping.*;

@RestController
public class PaymentTransactionServiceController implements PaymentTransactionServiceContract, ExternalController {

    final private PaymentTransactionRepository paymentTransactionRepository;

    public PaymentTransactionServiceController(final PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = checkNotNull(paymentTransactionRepository);
    }

    @RequestMapping(method = RequestMethod.GET, value = MY_TRANSACTIONS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<PaymentTransaction> myTransactions(@CookieValue("player") String player) {
        // Step 1. Sending transactions
        return paymentTransactionRepository.findByPaymentOperationsPlayer(player);
    }

    @RequestMapping(method = RequestMethod.GET, value = MY_TRANSACTIONS_BY_SOURCE, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<PaymentTransaction> myTransactions(@CookieValue("player") String player, @PathVariable("source") String source) {
        return paymentTransactionRepository.findByPaymentOperationsPlayerAndTransactionKeyLike(player, source);
    }


    @Override
    @RequestMapping(method = RequestMethod.GET, value = TRANSACTIONS_BY_ID, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public PaymentTransaction getTransaction(@PathVariable("transaction") String transactionKey) {
        // Step 1. Checking payment transaction exists
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findOne(transactionKey);
//        TODO PaymentTransactionNotExists exception might be overrated
//        if (paymentTransaction == null)
//            throw ClembleCasinoException.fromError(ClembleCasinoError.PaymentTransactionNotExists);
        return paymentTransaction;
    }
    

    @Override
    @RequestMapping(method = RequestMethod.GET, value = TRANSACTIONS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public List<PaymentTransaction> getPlayerTransactions(
        @PathVariable("player") String player
    ) {
        // Step 1. Sending transactions
        return paymentTransactionRepository.findByPaymentOperationsPlayer(player);
    }

}
