package dev.paintballplus;

import dev.paintballplus.listener.F5BlockListener;
import dev.paintballplus.listener.TabIndicatorListener;
import dev.paintballplus.tab.ModIndicator;
import dev.paintballplus.tab.PresenceApiClient;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class PaintballPlus extends LabyAddon<PaintballPlusConfig> {

    private ModIndicator modIndicator;

    @Override
    protected void enable() {
        this.registerSettingCategory();

        PerspectiveBlocker.initialize(this);

        this.modIndicator = new ModIndicator();
        PresenceApiClient presenceApiClient = new PresenceApiClient(this.modIndicator);
        GameDetector gameDetector = new GameDetector();

        this.registerListener(new F5BlockListener(this));
        this.registerListener(gameDetector);
        this.registerListener(presenceApiClient);
        this.registerListener(new TabIndicatorListener(this, this.modIndicator));
    }

    @Override
    protected Class<PaintballPlusConfig> configurationClass() {
        return PaintballPlusConfig.class;
    }
}
