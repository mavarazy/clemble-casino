package com.clemble.casino.integration.player;

import com.clemble.casino.android.player.AndroidPlayerFacadeRegistrationService;
import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.spring.IntegrationTestSpringConfiguration;
import com.clemble.casino.registration.service.PlayerFacadeRegistrationService;
import com.clemble.casino.test.util.ClembleCasinoExceptionMatcherFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by mavarazy on 1/14/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { IntegrationTestSpringConfiguration.class })
public class PlayerSignOutITest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    public PlayerScenarios playerScenarios;

    @Autowired
    public PlayerFacadeRegistrationService facadeRegistrationService;

    @Test
    public void testSignOut() {
        if (!(facadeRegistrationService instanceof AndroidPlayerFacadeRegistrationService))
            return;
        // Step 1. Creating player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Check that operations are forbidden, after signOut
        Assert.assertNotNull(A.profileOperations().myProfile());
        // Step 2.1. Sign out from application
        A.signOut();
        // Step 3. Checking profile operation is no longer allowed
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleCasinoError.ServerError));
        A.profileOperations().myProfile();
    }

}
