package com.gogomaya.server.game.build;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import javax.inject.Inject;

import com.gogomaya.server.error.GogomayaError;
import com.gogomaya.server.error.GogomayaException;
import com.gogomaya.server.game.GameState;
import com.gogomaya.server.game.GameTable;
import com.gogomaya.server.game.action.GameTableFactory;
import com.gogomaya.server.game.active.ActivePlayerQueue;
import com.gogomaya.server.game.event.server.GameStartedEvent;
import com.gogomaya.server.game.event.server.PlayerAddedEvent;
import com.gogomaya.server.game.notification.GameNotificationService;
import com.gogomaya.server.game.notification.TableServerRegistry;
import com.gogomaya.server.game.rule.construction.PlayerNumberRule;
import com.gogomaya.server.game.specification.GameSpecification;
import com.gogomaya.server.game.table.PendingSessionQueue;
import com.gogomaya.server.game.table.GameTableRepository;
import com.gogomaya.server.money.Money;
import com.gogomaya.server.money.Operation;
import com.gogomaya.server.player.wallet.WalletOperation;
import com.gogomaya.server.player.wallet.WalletTransactionManager;

public class GameConstructionServiceImpl<State extends GameState> implements GameConstructionService<State> {

    final private PendingSessionQueue tableManager;
    final private TableServerRegistry tableRegistry;
    final private ActivePlayerQueue activePlayerQueue;

    final private WalletTransactionManager walletTransactionManager;
    final private GameTableRepository<State> tableRepository;
    final private GameTableFactory<State> tableFactory;
    final private GameNotificationService<State> notificationManager;

    @Inject
    public GameConstructionServiceImpl(final PendingSessionQueue tableManager,
            final GameTableRepository<State> tableRepository,
            final GameNotificationService<State> notificationManager,
            final GameTableFactory<State> tableFactory,
            final WalletTransactionManager walletTransactionManager,
            final ActivePlayerQueue activePlayerQueue,
            final TableServerRegistry tableServerRegistry) {
        this.activePlayerQueue = checkNotNull(activePlayerQueue);
        this.tableManager = checkNotNull(tableManager);
        this.tableRepository = checkNotNull(tableRepository);
        this.notificationManager = checkNotNull(notificationManager);
        this.tableFactory = checkNotNull(tableFactory);
        this.walletTransactionManager = checkNotNull(walletTransactionManager);
        this.tableRegistry = tableServerRegistry;
    }

    @Override
    public GameTable<State> instantGame(final long playerId, final GameSpecification specification) {
        Long currentSession = activePlayerQueue.isActive(playerId);
        if (currentSession != null) {
            GameTable<State> table = tableRepository.findBySessionId(currentSession);
            if (table == null) {
                activePlayerQueue.markInActive(playerId);
            } else {
                return tableRegistry.specifyServer(table);
            }
        }
        // Step 0. Checking player can afford to play this game
        if (!walletTransactionManager.canAfford(playerId, extractMoneyNeeded(specification)))
            throw GogomayaException.create(GogomayaError.GameConstructionInsufficientMoney);
        // Step 1. Pooling
        Long session = tableManager.poll(specification);
        GameTable<State> table = tableFactory.findTable(session, specification);

        if (!activePlayerQueue.markActive(playerId, table.getCurrentSession().getSession()))
            throw GogomayaException.create(GogomayaError.GameMatchPlayerHasPendingSessions);
        table.addPlayer(playerId);
        table = tableRepository.save(table);

        notificationManager.notify(table.getPlayers(),
                new PlayerAddedEvent().setSession(table.getTableId()).setPlayerId(playerId));

        PlayerNumberRule numberRule = specification.getNumberRule();
        if (table.getPlayers().size() >= numberRule.getMinPlayers()) {
            tableFactory.startGame(table);
            tableRepository.saveAndFlush(table);
            // Step 3. Initializing start of the game session
            State state = table.getCurrentSession().getState();

            notificationManager.notify(table.getPlayers(),
                    new GameStartedEvent<State>().setNextMoves(state.getNextMoves()).setState(state).setSession(table.getTableId()));
        } else {
            tableManager.add(table.getTableId(), table.getSpecification());
        }

        return tableRegistry.specifyServer(table);
    }

    @Override
    public GameTable<State> avilabilityGame(long playerId, Collection<Long> opponenents, GameSpecification specification) {
        // Step 0. Checking player can afford to play this game
        if (!walletTransactionManager.canAfford(playerId, extractMoneyNeeded(specification)))
            throw GogomayaException.create(GogomayaError.GameConstructionInsufficientMoney);
        return null;
    }
    
    private Money extractMoneyNeeded(GameSpecification specification) {
        return new Money(specification.getCurrency(), specification.getBetRule().getPrice());
    }

}
