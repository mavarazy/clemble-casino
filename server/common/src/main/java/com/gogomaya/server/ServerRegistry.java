package com.gogomaya.server;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ServerRegistry implements Serializable {

    /**
     * Server registry
     */
    private static final long serialVersionUID = 8743316420554283274L;

    final private TreeMap<Long, String> SERVER_REGISTRY = new TreeMap<Long, String>();

    public ServerRegistry() {
    }

    public void register(Long lastId, String value) {
        SERVER_REGISTRY.put(lastId, value);
    }

    public String find(Long id) {
        // Step 1. Search for closest floor server connection
        Entry<Long, String> connectionEntry = SERVER_REGISTRY.ceilingEntry(id);
        if (connectionEntry != null)
            return connectionEntry.getValue();
        // Step 2. Search for closest lower server connection
        connectionEntry = SERVER_REGISTRY.floorEntry(id);
        return connectionEntry != null ? connectionEntry.getValue() : null;
    }

}
