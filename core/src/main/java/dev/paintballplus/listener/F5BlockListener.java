package dev.paintballplus.listener;

import dev.paintballplus.PaintballPlus;
import dev.paintballplus.PerspectiveBlocker;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import net.labymod.api.event.client.render.GameRenderEvent;
import net.labymod.api.event.client.render.camera.CameraSetupEvent;

public class F5BlockListener {

    private final PaintballPlus addon;

    public F5BlockListener(PaintballPlus addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onGameTick(GameTickEvent event) {
        if (event.phase() != Phase.PRE) return;
        if (!this.addon.configuration().enabled().get()) return;
        PerspectiveBlocker.block();
    }

    @Subscribe
    public void onGameRender(GameRenderEvent event) {
        if (event.phase() != Phase.PRE) return;
        if (!this.addon.configuration().enabled().get()) return;
        PerspectiveBlocker.block();
    }

    @Subscribe
    public void onCameraSetup(CameraSetupEvent event) {
        if (event.phase() != Phase.PRE) return;
        if (!this.addon.configuration().enabled().get()) return;
        PerspectiveBlocker.block();
    }

    @Subscribe
    public void onKeyPress(KeyEvent event) {
        if (!this.addon.configuration().enabled().get()) return;
        if (!PerspectiveBlocker.isGameplayInputActive()) return;

        boolean consumedPerspectiveToggle = event.state() == KeyEvent.State.PRESS && PerspectiveBlocker.consumePerspectiveTogglePresses();

        if (consumedPerspectiveToggle || event.key() == Key.F5 || PerspectiveBlocker.isPerspectiveToggleKey(event.key())) {
            event.setCancelled(true);
        }

        if (consumedPerspectiveToggle) {
            PerspectiveBlocker.block();
        }
    }
}
