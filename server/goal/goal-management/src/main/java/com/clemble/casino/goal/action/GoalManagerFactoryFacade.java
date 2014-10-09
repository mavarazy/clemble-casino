package com.clemble.casino.goal.action;

import com.clemble.casino.goal.event.GoalEvent;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.initiation.GoalInitiation;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.repository.GoalRecordRepository;
import com.clemble.casino.goal.repository.GoalStateRepository;
import com.clemble.casino.server.action.ClembleManager;
import com.clemble.casino.server.action.ClembleManagerFactory;
import com.clemble.casino.server.player.notification.PlayerNotificationService;

/**
 * Created by mavarazy on 9/20/14.
 */
public class GoalManagerFactoryFacade {

    final private GoalRecordRepository recordRepository;
    final private SelfGoalManagerFactory selfManagerFactory;

    public GoalManagerFactoryFacade(
        ClembleManagerFactory<GoalConfiguration> managerFactory,
        GoalRecordRepository recordRepository,
        GoalStateRepository stateRepository,
        PlayerNotificationService notificationService) {
        this.recordRepository = recordRepository;
        this.selfManagerFactory = new SelfGoalManagerFactory(managerFactory, recordRepository, stateRepository, notificationService);
    }

    public ClembleManager<GoalEvent, ? extends GoalState> start(GoalInitiation initiation) {
        if (initiation.getPlayer().equals(initiation.getJudge())) {
            return selfManagerFactory.start(initiation);
        }
        throw new IllegalArgumentException();
    }

}
