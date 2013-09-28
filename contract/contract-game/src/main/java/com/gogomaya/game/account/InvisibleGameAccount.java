package com.gogomaya.game.account;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gogomaya.money.Money;
import com.gogomaya.player.PlayerAwareUtils;

@JsonTypeName("invisible")
@JsonIgnoreProperties("playerAccounts")
public class InvisibleGameAccount implements GameAccount {

    private static final long serialVersionUID = -6227399075601837719L;

    final private Money bank;
    final private Map<String, GamePlayerAccount> playerToAccount;

    @JsonCreator
    public InvisibleGameAccount(@JsonProperty("bank") Money bank, @JsonProperty("playerAccounts") Collection<GamePlayerAccount> playerAccounts) {
        this.bank = bank;
        this.playerToAccount = PlayerAwareUtils.toImmutableMap(playerAccounts);
    }

    @Override
    final public Money getBank() {
        return bank;
    }

    @Override
    final public Collection<GamePlayerAccount> getPlayerAccounts() {
        return playerToAccount.values();
    }

    @Override
    final public GamePlayerAccount getPlayerAccount(String player) {
        return playerToAccount.get(player);
    }

    @Override
    final public void subMoneyLeft(String player, long amount) {
        playerToAccount.get(player).subMoneyLeft(amount);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bank == null) ? 0 : bank.hashCode());
        result = prime * result + ((playerToAccount == null) ? 0 : playerToAccount.hashCode());
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
        InvisibleGameAccount other = (InvisibleGameAccount) obj;
        if (bank == null) {
            if (other.bank != null)
                return false;
        } else if (!bank.equals(other.bank))
            return false;
        if (playerToAccount == null) {
            if (other.playerToAccount != null)
                return false;
        } else if (!playerToAccount.equals(other.playerToAccount))
            return false;
        return true;
    }

}
