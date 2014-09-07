package com.clemble.casino.server.game.construction.auto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.error.ClembleCasinoException;
import com.clemble.casino.game.construct.AutomaticGameRequest;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.construct.GameInitiation;
import com.clemble.casino.game.service.AutoGameConstructionService;
import com.clemble.casino.game.configuration.GameConfiguration;
import com.clemble.casino.player.PlayerPresence;
import com.clemble.casino.player.Presence;
import com.clemble.casino.server.event.game.SystemGameReadyEvent;
import com.clemble.casino.server.game.construction.GameSessionKeyGenerator;
import com.clemble.casino.server.game.construction.repository.GameConstructionRepository;
import com.clemble.casino.server.player.lock.PlayerLockService;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.player.presence.ServerPlayerPresenceService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ServerAutoGameConstructionService implements AutoGameConstructionService {

    final public static class AutomaticGameConstruction {
        final private GameConstruction construction;
        final private GameConfiguration configuration;
        final private List<String> participants = new ArrayList<>();

        public AutomaticGameConstruction(GameConstruction construction) {
            this.construction = construction;
            this.configuration = construction.getRequest().getConfiguration();
            this.participants.add(((AutomaticGameRequest) construction.getRequest()).getPlayer());
        }

        public GameConstruction getConstruction() {
            return construction;
        }

        public boolean append(AutomaticGameRequest request) {
            if (request != null) {
                participants.add(request.getPlayer());
            }
            return participants.size() >= configuration.getNumberRule().getMinPlayers();
        }

        public boolean ready() {
            return configuration.getNumberRule().getMinPlayers() <= participants.size();
        }

        public GameInitiation toInitiation() {
            // Step 1. Creating instant game request
            return new GameInitiation(construction.getSessionKey(), participants, configuration);
        }
    }

    final private LoadingCache<String, Queue<AutomaticGameConstruction>> PENDING_CONSTRUCTIONS = CacheBuilder.newBuilder().build(
            new CacheLoader<String, Queue<AutomaticGameConstruction>>() {
                @Override
                public Queue<AutomaticGameConstruction> load(String key) throws Exception {
                    return new ArrayBlockingQueue<AutomaticGameConstruction>(100);
                }
            });

    final private Map<String, AutomaticGameConstruction> playerConstructions = new ConcurrentHashMap<>();

    final private GameSessionKeyGenerator sessionKeyGenerator;

    final private GameConstructionRepository constructionRepository;
    final private SystemNotificationService notificationService;

    final private PlayerLockService playerLockService;
    final private ServerPlayerPresenceService playerStateManager;

    public ServerAutoGameConstructionService(
            final GameSessionKeyGenerator sessionKeyGenerator,
            final SystemNotificationService initiatorService,
            final GameConstructionRepository constructionRepository,
            final PlayerLockService playerLockService,
            final ServerPlayerPresenceService playerStateManager) {
        this.sessionKeyGenerator = checkNotNull(sessionKeyGenerator);
        this.notificationService = checkNotNull(initiatorService);
        this.constructionRepository = checkNotNull(constructionRepository);
        this.playerLockService = checkNotNull(playerLockService);
        this.playerStateManager = checkNotNull(playerStateManager);
    }

    @Override
    public GameConstruction construct(AutomaticGameRequest request) {
        // Step 1. Sanity check
        if (request == null)
            throw ClembleCasinoException.fromError(ClembleCasinoError.GameConstructionInvalidState);
        PlayerPresence playerPresence = playerStateManager.getPresence(request.getPlayer());
        if (playerPresence.getPresence() == Presence.playing) {
            GameConstruction activeConstruction = constructionRepository.findOne(playerPresence.getSessionKey());
            if (activeConstruction != null)
                return activeConstruction;
        }
        // Step 2. Fetching associated Queue
        String player = request.getPlayer();
        playerLockService.lock(player);
        AutomaticGameConstruction pendingConstuction;
        try {
            pendingConstuction = playerConstructions.get(player);
            // Step 2.1 Check there is no pending constructions for the user
            if (pendingConstuction != null)
                return pendingConstuction.getConstruction();
            // Step 2.2. Acquire and process Queue
            Queue<AutomaticGameConstruction> specificationQueue = null;
            try {
                String constructionKey = request.getConfiguration().getConfigurationKey();
                specificationQueue = PENDING_CONSTRUCTIONS.get(constructionKey);
            } catch (ExecutionException e) {
                throw ClembleCasinoException.fromError(ClembleCasinoError.ServerCacheError);
            }
            // Step 3. Processing request
            pendingConstuction = specificationQueue.poll();
            if (pendingConstuction == null) {
                // Step 3.1 Construction was not present, creating new one
                GameConstruction construction = GameConstruction.fromRequest(sessionKeyGenerator.generate(request.getConfiguration()), request);
                construction = constructionRepository.save(construction);

                pendingConstuction = new AutomaticGameConstruction(construction);
                playerConstructions.put(player, pendingConstuction);
                specificationQueue.add(pendingConstuction);
            } else {
                // Step 3.2 Construction was present appending to existing one
                if (pendingConstuction.append(request)) {
                    GameInitiation initiation = pendingConstuction.toInitiation();
                    notificationService.notify(new SystemGameReadyEvent(initiation));
                    for (String participant : initiation.getParticipants())
                        playerConstructions.remove(participant);
                } else {
                    playerConstructions.put(player, pendingConstuction);
                    specificationQueue.add(pendingConstuction);
                }
            }
        } finally {
            playerLockService.unlock(player);
        }
        return pendingConstuction.getConstruction();
    }

    public Collection<GameConstruction> getPending(String player) {
        // Step 1. Fetching AutomaticGameConstruction
        AutomaticGameConstruction construction = playerConstructions.get(player);
        if(construction == null)
            return Collections.emptyList();
        // Step 2. Extracting GameConstruction and returning as singleton
        return Collections.singletonList(construction.getConstruction());
    }

}
