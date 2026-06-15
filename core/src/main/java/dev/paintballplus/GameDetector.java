package dev.paintballplus;

import net.labymod.api.Laby;
import net.labymod.api.client.component.serializer.plain.PlainTextComponentSerializer;
import net.labymod.api.client.scoreboard.DisplaySlot;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.event.client.network.server.ServerJoinEvent;

public class GameDetector {

    private static final String MINEPLEX_HOST = "mineplex.com";
    private static final String PAINTBALL_KEYWORD = "PAINTBALL";

    private boolean onMineplex = false;
    private boolean paintballDetected = false;

    public boolean isInPaintballGame() {
        return onMineplex && paintballDetected;
    }

    public boolean isOnMineplex() {
        return onMineplex;
    }

    public void tick() {
        if (!onMineplex || paintballDetected) return;
        var scoreboard = Laby.labyAPI().minecraft().getScoreboard();
        if (scoreboard == null) return;
        var sidebar = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (sidebar == null) return;
        var title = sidebar.getTitle();
        if (title == null) return;
        if (PlainTextComponentSerializer.plainText().serialize(title).contains(PAINTBALL_KEYWORD)) {
            paintballDetected = true;
        }
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) {
        String host = event.serverData().address().getHost();
        this.onMineplex = host != null && host.toLowerCase().contains(MINEPLEX_HOST);
        this.paintballDetected = false;
    }

    @Subscribe
    public void onServerDisconnect(ServerDisconnectEvent event) {
        this.onMineplex = false;
        this.paintballDetected = false;
    }
}
