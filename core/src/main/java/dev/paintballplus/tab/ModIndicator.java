package dev.paintballplus.tab;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which players in the current server session are running Paintball+.
 *
 * Phase 1 (current): local-only — only the client themselves are marked.
 * Phase 2 (TODO): ping a lightweight verification server on game join to
 *   register this UUID, and poll for other registered UUIDs in the same
 *   Mineplex session. See README for the planned API contract.
 */
public class ModIndicator {

    // UUIDs of players confirmed to be running Paintball+
    private final Set<UUID> modUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ModIndicator() {
        // Phase 2: get local UUID via Laby.labyAPI().minecraft().selfPlayer().getUniqueId()
    }

    public boolean isModUser(UUID uuid) {
        return modUsers.contains(uuid);
    }

    public void addModUser(UUID uuid) {
        modUsers.add(uuid);
    }

    public void clearRemotePlayers(UUID localUUID) {
        modUsers.removeIf(uuid -> !uuid.equals(localUUID));
    }

    public Set<UUID> getModUsers() {
        return Collections.unmodifiableSet(modUsers);
    }
}
