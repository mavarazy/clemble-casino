package com.clemble.casino.goal.spring;

import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalContext;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.repository.ShortGoalStateRepository;
import com.clemble.casino.payment.Bank;
import com.clemble.test.random.ObjectGenerator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parboiled.common.ImmutableLinkedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import scala.collection.mutable.HashSet;

import java.util.Collections;
import java.util.Set;

/**
 * Created by mavarazy on 12/14/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { GoalManagementSpringConfiguration.class })
public class GoalStateRepositoryTest {

    @Autowired
    public ShortGoalStateRepository stateRepository;

    @Test
    public void simpleTest() {
        // Step 1. Generating short goal state
        GoalState shortGoalState = new GoalState(
            ObjectGenerator.generate(String.class),
            ObjectGenerator.generate(String.class),
            ObjectGenerator.generate(Bank.class),
            ObjectGenerator.generate(String.class),
            ObjectGenerator.generate(GoalConfiguration.class),
            new GoalContext(null, Collections.emptyList()),
            Collections.<String>singleton(ObjectGenerator.generate(String.class)),
            ObjectGenerator.generate(boolean.class),
            ObjectGenerator.generate(String.class)
        );
        // Step 2. Saving short goal state
        stateRepository.save(shortGoalState);
        // Step 3. Checking short goal state, can be fetched
        GoalState goalState = (GoalState) stateRepository.findByPlayer(shortGoalState.getPlayer()).get(0);
        // Step 4. Check values match
        Assert.assertEquals(goalState, shortGoalState);
    }

}
