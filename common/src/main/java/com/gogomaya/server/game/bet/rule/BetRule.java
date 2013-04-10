package com.gogomaya.server.game.bet.rule;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

import com.gogomaya.server.game.GameRuleOptions;
import com.gogomaya.server.game.rule.GameRule;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "betType")
@JsonSubTypes({ @Type(name = "fixed", value = BetFixedRule.class), @Type(name = "limited", value = BetLimitedRule.class),
        @Type(name = "unlimited", value = BetUnlimitedRule.class) })
abstract public class BetRule implements GameRule {

    /**
     * Generated 09/04/13
     */
    private static final long serialVersionUID = -1458557269866528512L;

    final public static BetRule DEFAULT = BetFixedRule.create(50);
    final public static GameRuleOptions<BetRule> DEFAULT_OPTIONS = new GameRuleOptions<BetRule>(BetFixedRule.create(50), BetFixedRule.create(100), BetFixedRule.create(200));

    protected BetRule() {
    }

}
