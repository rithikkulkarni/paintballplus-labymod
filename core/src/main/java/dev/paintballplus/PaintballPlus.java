package dev.paintballplus;

import dev.paintballplus.listener.F5BlockListener;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class PaintballPlus extends LabyAddon<PaintballPlusConfig> {

    @Override
    protected void enable() {
        PerspectiveBlocker.initialize(this);
        this.registerListener(new F5BlockListener(this));
    }

    @Override
    protected Class<PaintballPlusConfig> configurationClass() {
        return PaintballPlusConfig.class;
    }
}
