package com.gogomaya.server.game.rule.giveup;

import com.gogomaya.server.game.configuration.GameRuleOptions;
import com.gogomaya.server.game.rule.GameRule;

public enum GiveUpRule implements GameRule {

    lost,
    all,
    half,
    therd,
    quarter,
    tenth;

    final public static GiveUpRule DEFAULT = GiveUpRule.all;
    final public static GameRuleOptions<GiveUpRule> DEFAULT_OPTIONS = new GameRuleOptions<GiveUpRule>(tenth, all, half, therd, quarter, tenth);
}
