package dev.paintballplus.listener;

import dev.paintballplus.PaintballPlus;
import dev.paintballplus.tab.ModIndicator;
import java.util.UUID;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent.Context;

public class TabIndicatorListener {

    private final PaintballPlus addon;
    private final ModIndicator modIndicator;

    public TabIndicatorListener(PaintballPlus addon, ModIndicator modIndicator) {
        this.addon = addon;
        this.modIndicator = modIndicator;
    }

    @Subscribe
    public void onPlayerNameTagRender(PlayerNameTagRenderEvent event) {
        if (event.context() != Context.TAB_LIST) return;
        if (!this.addon.configuration().showTabIndicator().get()) return;

        NetworkPlayerInfo playerInfo = event.getPlayerInfo();
        if (playerInfo == null) return;

        UUID uuid = playerInfo.profile().getUniqueId();
        if (!modIndicator.isModUser(uuid)) return;

        Component badge = Component.text("[P+] ", NamedTextColor.AQUA);
        event.setNameTag(badge.append(event.nameTag()));
    }
}
