package com.gogomaya.server.payment;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;

import com.gogomaya.server.money.Currency;
import com.gogomaya.server.money.Money;
import com.gogomaya.server.money.MoneyHibernate;

@Entity
@Table(name = "PAYMENT_TRANSACTION")
@TypeDef(name = "money", typeClass = MoneyHibernate.class)
public class PaymentTransaction implements Serializable {

    /**
     * Generated 05/05/13
     */
    private static final long serialVersionUID = 2610517770966910840L;

    @EmbeddedId
    private PaymentTransactionId transactionId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PAYMENT_TRANSACTION_OPERATION", joinColumns = { @JoinColumn(name = "TRANSACTION_ID"), @JoinColumn(name = "MONEY_SOURCE") })
    private Set<PaymentOperation> paymentOperations = new HashSet<PaymentOperation>();

    @Column(name = "TRANSACTION_DATE")
    private Date transactionDate;

    public PaymentTransactionId getTransactionId() {
        return transactionId;
    }

    public PaymentTransaction setTransactionId(PaymentTransactionId transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public boolean isParticipant(long playerId) {
        for (PaymentOperation paymentOperation: paymentOperations)
            if (paymentOperation.getPlayerId() == playerId)
                return true;
        return false;
    }

    public Set<PaymentOperation> getPaymentOperations() {
        return paymentOperations;
    }

    public PaymentTransaction setPaymentOperations(Set<PaymentOperation> paymentOperations) {
        this.paymentOperations = paymentOperations;
        return this;
    }

    public PaymentTransaction addPaymentOperation(PaymentOperation paymentOperation) {
        if (paymentOperation != null)
            this.paymentOperations.add(paymentOperation);
        return this;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public PaymentTransaction setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public boolean valid() {
        // Step 1. Checking currency
        Currency currency = null;
        for (PaymentOperation paymentOperation : paymentOperations) {
            if (currency == null) {
                currency = paymentOperation.getAmmount().getCurrency();
            } else if (currency != paymentOperation.getAmmount().getCurrency()) {
                return false;
            }
        }
        // Step 2. Checking credit and debit ammount match up
        Money creditAmmount = null;
        Money debitAmmount = null;
        for (PaymentOperation paymentOperation : paymentOperations) {
            Money ammount = paymentOperation.getAmmount();
            if (ammount.getAmount() > 0) {
                switch (paymentOperation.getOperation()) {
                case Credit:
                    creditAmmount = creditAmmount == null ? paymentOperation.getAmmount() : creditAmmount.add(ammount);
                    break;
                case Debit:
                    debitAmmount = debitAmmount == null ? paymentOperation.getAmmount() : debitAmmount.add(paymentOperation.getAmmount());
                    break;
                }
            } else {
                ammount = ammount.negate();
                switch (paymentOperation.getOperation()) {
                case Credit:
                    debitAmmount = debitAmmount == null ? paymentOperation.getAmmount() : debitAmmount.add(paymentOperation.getAmmount());
                    break;
                case Debit:
                    creditAmmount = creditAmmount == null ? paymentOperation.getAmmount() : creditAmmount.add(ammount);
                    break;
                }
            }
        }
        return (creditAmmount != null && debitAmmount != null) && creditAmmount.getAmount() == debitAmmount.getAmount()
                && creditAmmount.getCurrency() == debitAmmount.getCurrency();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((transactionDate == null) ? 0 : transactionDate.hashCode());
        result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
        result = prime * result + ((paymentOperations == null) ? 0 : paymentOperations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaymentTransaction other = (PaymentTransaction) obj;
        if (transactionDate == null) {
            if (other.transactionDate != null)
                return false;
        } else if (!transactionDate.equals(other.transactionDate))
            return false;
        if (transactionId == null) {
            if (other.transactionId != null)
                return false;
        } else if (!transactionId.equals(other.transactionId))
            return false;
        if (paymentOperations == null) {
            if (other.paymentOperations != null)
                return false;
        } else if (!paymentOperations.equals(other.paymentOperations))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PaymentTransaction [transactionId=" + transactionId + ", paymentOperations=" + paymentOperations + ", transactionDate=" + transactionDate + "]";
    }

}
