package dev.paintballplus.tab;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.labymod.api.Laby;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.event.client.network.server.ServerJoinEvent;

/**
 * Registers this client's presence with the Paintball+ API and polls for
 * other mod users on the same server.
 *
 * Deploy a backend at API_BASE that implements:
 *   POST /presence  body: {"uuid":"...","username":"...","server":"..."}
 *   GET  /presence?server=...  response: {"players":["uuid1","uuid2",...]}
 *
 * A Cloudflare Worker + KV store is the recommended lightweight backend.
 */
public class PresenceApiClient {

    // Replace with your deployed backend URL
    private static final String API_BASE = "https://paintballplus-presence.paintballplus-presence.workers.dev";
    private static final int POLL_INTERVAL_SECONDS = 5;

    private final ModIndicator modIndicator;
    private final HttpClient httpClient;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pollTask;
    private String currentServer;

    public PresenceApiClient(ModIndicator modIndicator) {
        this.modIndicator = modIndicator;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "paintballplus-presence");
            t.setDaemon(true);
            return t;
        });
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) {
        currentServer = event.serverData().address().getHost();

        var player = Laby.labyAPI().minecraft().getClientPlayer();
        if (player == null) return;

        UUID uuid = player.getUniqueId();
        String username = player.getName();

        modIndicator.addModUser(uuid);
        registerPresence(uuid, username, currentServer);

        stopPolling();
        pollTask = scheduler.scheduleAtFixedRate(
            () -> pollPresence(currentServer, uuid),
            POLL_INTERVAL_SECONDS, POLL_INTERVAL_SECONDS, TimeUnit.SECONDS
        );
    }

    @Subscribe
    public void onServerDisconnect(ServerDisconnectEvent event) {
        stopPolling();
        var player = Laby.labyAPI().minecraft().getClientPlayer();
        UUID localUUID = player != null ? player.getUniqueId() : null;
        if (localUUID != null) {
            modIndicator.clearRemotePlayers(localUUID);
        }
        currentServer = null;
    }

    private void registerPresence(UUID uuid, String username, String server) {
        String body = String.format(
            "{\"uuid\":\"%s\",\"username\":\"%s\",\"server\":\"%s\"}",
            uuid, escapeJson(username), escapeJson(server)
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_BASE + "/presence"))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(body))
            .timeout(Duration.ofSeconds(5))
            .build();
        httpClient.sendAsync(request, BodyHandlers.discarding())
            .exceptionally(ex -> null);
    }

    private void pollPresence(String server, UUID localUUID) {
        String encodedServer = URLEncoder.encode(server, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_BASE + "/presence?server=" + encodedServer))
            .GET()
            .timeout(Duration.ofSeconds(5))
            .build();
        httpClient.sendAsync(request, BodyHandlers.ofString())
            .thenAccept(response -> {
                if (response.statusCode() == 200) {
                    parseAndUpdatePlayers(response.body(), localUUID);
                }
            })
            .exceptionally(ex -> null);
    }

    private void parseAndUpdatePlayers(String json, UUID localUUID) {
        modIndicator.clearRemotePlayers(localUUID);

        int start = json.indexOf('[');
        int end = json.lastIndexOf(']');
        if (start < 0 || end <= start) return;

        String arrayPart = json.substring(start + 1, end).trim();
        if (arrayPart.isEmpty()) return;

        for (String part : arrayPart.split(",")) {
            String uuidStr = part.trim().replace("\"", "");
            if (!uuidStr.isEmpty()) {
                try {
                    modIndicator.addModUser(UUID.fromString(uuidStr));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    private void stopPolling() {
        if (pollTask != null) {
            pollTask.cancel(false);
            pollTask = null;
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
