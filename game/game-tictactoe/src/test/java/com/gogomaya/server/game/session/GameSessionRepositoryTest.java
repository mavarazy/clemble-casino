package com.gogomaya.server.game.session;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gogomaya.server.game.Game;
import com.gogomaya.server.game.GameSession;
import com.gogomaya.server.game.GameTable;
import com.gogomaya.server.game.rule.bet.FixedBetRule;
import com.gogomaya.server.game.rule.construction.PlayerNumberRule;
import com.gogomaya.server.game.rule.construction.PrivacyRule;
import com.gogomaya.server.game.rule.giveup.GiveUpRule;
import com.gogomaya.server.game.rule.time.MoveTimeRule;
import com.gogomaya.server.game.rule.time.TotalTimeRule;
import com.gogomaya.server.game.specification.GameSpecification;
import com.gogomaya.server.game.specification.SpecificationName;
import com.gogomaya.server.game.tictactoe.TicTacToeState;
import com.gogomaya.server.money.Currency;
import com.gogomaya.server.repository.game.GameSessionRepository;
import com.gogomaya.server.repository.game.GameSpecificationRepository;
import com.gogomaya.server.repository.game.GameTableRepository;
import com.gogomaya.server.spring.common.SpringConfiguration;
import com.gogomaya.server.spring.tictactoe.TicTacToeSpringConfiguration;
import com.gogomaya.server.tictactoe.action.impl.TicTacToeStateFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(SpringConfiguration.PROFILE_TEST)
@ContextConfiguration(classes = { TicTacToeSpringConfiguration.class })
public class GameSessionRepositoryTest {

    GameSpecification DEFAULT_SPECIFICATION = new GameSpecification().setName(new SpecificationName(Game.pic, "DEFAULT")).setBetRule(new FixedBetRule(50))
            .setCurrency(Currency.FakeMoney).setGiveUpRule(GiveUpRule.lost).setMoveTimeRule(MoveTimeRule.DEFAULT)
            .setTotalTimeRule(TotalTimeRule.DEFAULT).setNumberRule(PlayerNumberRule.two).setPrivacayRule(PrivacyRule.everybody);;

    @Inject
    GameSessionRepository<TicTacToeState> sessionRepository;

    @Inject
    GameTableRepository<TicTacToeState> tableRepository;

    @Inject
    TicTacToeStateFactory stateFactory;

    @Inject
    GameSpecificationRepository specificationRepository;

    @Before
    public void setUp() {
        specificationRepository.saveAndFlush(DEFAULT_SPECIFICATION);
    }

    @Test
    public void testSaveSessionWithState() {
        List<Long> players = new ArrayList<Long>();
        players.add(1L);
        players.add(2L);

        TicTacToeState gameState = stateFactory.constructState(DEFAULT_SPECIFICATION, players);

        GameTable<TicTacToeState> gameTable = new GameTable<TicTacToeState>();
        gameTable.setSpecification(DEFAULT_SPECIFICATION);
        gameTable.setPlayers(players);

        gameTable.setCurrentSession(new GameSession<TicTacToeState>());
        
        gameTable = tableRepository.save(gameTable);

        Assert.assertNotNull(gameTable.getCurrentSession());
        gameTable.getCurrentSession().setState(gameState);

        GameSession<TicTacToeState> gameSession = new GameSession<TicTacToeState>();
        gameSession.setPlayers(players);
        gameSession.setSpecification(DEFAULT_SPECIFICATION);

        gameSession = sessionRepository.save(gameSession);

        Assert.assertNotNull(gameSession);
        Assert.assertNotNull(gameTable.getCurrentSession().getState());
    }
}
