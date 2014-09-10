package com.clemble.casino.server.game.construction;

import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.UUID;

import com.clemble.casino.game.construction.event.InvitationResponseEvent;
import com.clemble.casino.game.configuration.RoundGameConfiguration;
import com.clemble.casino.server.game.construction.repository.GameConstructionRepository;
import com.clemble.casino.server.game.construction.spring.GameConstructionSpringConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clemble.casino.ActionLatch;
import com.clemble.casino.game.construction.AvailabilityGameRequest;
import com.clemble.casino.game.construction.GameConstruction;
import com.clemble.casino.game.construction.GameDeclineBehavior;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = SpringConfiguration.TEST)
@ContextConfiguration(classes = { GameConstructionSpringConfiguration.class })
public class GameConstructionRepositoryTest {

    @Autowired
    public GameConstructionRepository constructionRepository;

    @Autowired
    public ObjectMapper objectMapper;

    @Test
    public void testActionLatchSerialization() throws JsonParseException, JsonMappingException, IOException {
        String serializedLatch = null;
        try {
            ActionLatch randomLatch = new ActionLatch().expectNext(ImmutableList.<String>of("1", "2"), InvitationResponseEvent.class);
            serializedLatch = objectMapper.writeValueAsString(randomLatch);
            ActionLatch readLatch = objectMapper.readValue(serializedLatch, ActionLatch.class);
            Assert.assertEquals("Failed to deserialize: " + serializedLatch, readLatch, randomLatch);
        } catch (JsonMappingException mappingException) {
            mappingException.printStackTrace();
            Assert.fail("Failed to instantiate: " + serializedLatch);
        }
    }

    @Test
    public void testSaving() {
        AvailabilityGameRequest availabilityGameRequest = new AvailabilityGameRequest("1", RoundGameConfiguration.DEFAULT, ImmutableList.<String> of("1", "2"), GameDeclineBehavior.invalidate);

        GameConstruction construction = availabilityGameRequest.toConstruction(UUID.randomUUID().toString());
        construction.getResponses().expectNext("1", InvitationResponseEvent.class);
        Assert.assertNotNull(construction.getResponses());
        construction = constructionRepository.save(construction);
        Assert.assertNotNull(construction.getResponses());
    }

    @Test
    public void testSaving2() {
        AvailabilityGameRequest availabilityGameRequest = new AvailabilityGameRequest("1", RoundGameConfiguration.DEFAULT, ImmutableList.<String> of("1", "2"), GameDeclineBehavior.invalidate);

        GameConstruction construction = availabilityGameRequest.toConstruction((UUID.randomUUID().toString()));
        construction.getResponses().expectNext("1", InvitationResponseEvent.class);
        Assert.assertNotNull(construction.getResponses());
        construction = constructionRepository.save(construction);
        Assert.assertNotNull(construction.getResponses());

        GameConstruction anotherConstruction = availabilityGameRequest.toConstruction((UUID.randomUUID().toString()));
        anotherConstruction.getResponses().expectNext("1", InvitationResponseEvent.class);
        Assert.assertNotNull(anotherConstruction.getResponses());
        anotherConstruction = constructionRepository.save(anotherConstruction);
        Assert.assertNotNull(anotherConstruction.getResponses());
        
        assertNotEquals(anotherConstruction.getSessionKey(), construction.getSessionKey());

    }
}
