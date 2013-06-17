package com.gogomaya.server.game.table;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;

import com.gogomaya.server.error.GogomayaError;
import com.gogomaya.server.error.GogomayaException;
import com.gogomaya.server.game.specification.GameSpecification;
import com.gogomaya.server.game.specification.SpecificationName;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class JavaPendingSessionQueue implements PendingSessionQueue {

    final private LoadingCache<SpecificationName, Queue<Long>> PENDING_SESSIONS_CACHE = CacheBuilder.newBuilder().build(
            new CacheLoader<SpecificationName, Queue<Long>>() {
                @Override
                public Queue<Long> load(SpecificationName key) throws Exception {
                    return new ArrayBlockingQueue<Long>(100);
                }
            });

    @Override
    public Long poll(GameSpecification specification) {
        try {
            return PENDING_SESSIONS_CACHE.get(specification.getName()).poll();
        } catch (ExecutionException e) {
            throw GogomayaException.fromError(GogomayaError.GameConstructionTableQueuePutError);
        }
    }

    @Override
    public void add(long session, GameSpecification specification) {
        try {
            PENDING_SESSIONS_CACHE.get(specification.getName()).add(session);
        } catch (ExecutionException e) {
            throw GogomayaException.fromError(GogomayaError.GameConstructionTableQueueAddError);
        }
    }

    @Override
    public void invalidate(long session, GameSpecification specification) {
        try {
            PENDING_SESSIONS_CACHE.get(specification.getName()).remove(session);
        } catch (ExecutionException e) {
            throw GogomayaException.fromError(GogomayaError.GameConstructionTableQueueInvalidateError);
        }
    }

}
