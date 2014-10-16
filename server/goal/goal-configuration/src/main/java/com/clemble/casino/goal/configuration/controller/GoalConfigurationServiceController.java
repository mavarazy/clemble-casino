package com.clemble.casino.goal.configuration.controller;

import com.clemble.casino.bet.Bid;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.rule.judge.JudgeRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.parts.GoalPartsRule;
import com.clemble.casino.goal.lifecycle.configuration.service.GoalConfigurationService;
import com.clemble.casino.goal.lifecycle.configuration.rule.judge.JudgeType;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import com.clemble.casino.lifecycle.configuration.rule.bet.LimitedBetRule;
import com.clemble.casino.lifecycle.configuration.rule.breach.LooseBreachPunishment;
import com.clemble.casino.lifecycle.configuration.rule.privacy.PrivacyRule;
import com.clemble.casino.lifecycle.configuration.rule.time.MoveTimeRule;
import com.clemble.casino.lifecycle.configuration.rule.time.TotalTimeRule;
import com.google.common.collect.ImmutableList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.clemble.casino.goal.GoalWebMapping.MY_CONFIGURATIONS;
import static com.clemble.casino.WebMapping.PRODUCES;

/**
 * Created by mavarazy on 9/1/14.
 */
@RestController
public class GoalConfigurationServiceController<T extends GoalConfiguration> implements GoalConfigurationService<T> {

    // TODO replace with SMART configurations
    final private static List<GoalConfiguration> DEFAULT_CONFIGURATIONS = ImmutableList.of(
        new GoalConfiguration(
            "basic",
            new Bid(Money.create(Currency.FakeMoney, 50), Money.create(Currency.FakeMoney, 5)),
            LimitedBetRule.create(5, 50),
            new JudgeRule("me", JudgeType.self),
            new GoalPartsRule(1),
            new MoveTimeRule(TimeUnit.MINUTES.toMillis(1), LooseBreachPunishment.getInstance()),
            new TotalTimeRule(TimeUnit.MINUTES.toMillis(2), LooseBreachPunishment.getInstance()),
            PrivacyRule.players
        ),
        new GoalConfiguration(
            "advanced",
            new Bid(Money.create(Currency.FakeMoney, 5000), Money.create(Currency.FakeMoney, 5000)),
            LimitedBetRule.create(5, 50),
            new JudgeRule("me", JudgeType.self),
            new GoalPartsRule(1),
            new MoveTimeRule(TimeUnit.DAYS.toMillis(1), LooseBreachPunishment.getInstance()),
            new TotalTimeRule(TimeUnit.DAYS.toMillis(7), LooseBreachPunishment.getInstance()),
            PrivacyRule.players
        )
    );

    @RequestMapping(method = RequestMethod.GET, value = MY_CONFIGURATIONS, produces = PRODUCES)
    @ResponseStatus(HttpStatus.OK)
    public List<T> getConfigurations() {
        return (List<T>) DEFAULT_CONFIGURATIONS;
    }

}
