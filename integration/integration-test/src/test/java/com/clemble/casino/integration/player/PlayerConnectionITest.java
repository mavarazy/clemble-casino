package com.clemble.casino.integration.player;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.spring.IntegrationTestSpringConfiguration;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.test.concurrent.AsyncCompletionUtils;
import com.clemble.test.concurrent.Get;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;
import java.util.List;

/**
 * Created by mavarazy on 7/31/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { IntegrationTestSpringConfiguration.class })
public class PlayerConnectionITest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Test
    public void testEmptyConnections() {
        final ClembleCasinoOperations A = playerScenarios.createPlayer();
        final Collection<ConnectionKey> aConnections = A.connectionOperations().getConnectionIds();
        Assert.assertNotNull(aConnections);
        Assert.assertEquals(aConnections.size(), 0);
    }

}
