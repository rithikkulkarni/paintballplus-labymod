package dev.paintballplus;

import net.labymod.api.Laby;
import net.labymod.api.client.options.MinecraftInputMapping;
import net.labymod.api.client.options.MinecraftOptions;
import net.labymod.api.client.options.Perspective;

public final class PerspectiveBlocker {

    private static final String TOGGLE_PERSPECTIVE_MAPPING = "key.togglePerspective";

    private static PaintballPlus addon;

    private PerspectiveBlocker() {
    }

    public static void initialize(PaintballPlus addon) {
        PerspectiveBlocker.addon = addon;
    }

    public static boolean isEnabled() {
        return addon != null && addon.configuration().enabled().get();
    }

    public static void blockIfEnabled() {
        if (!isEnabled()) return;
        block();
    }

    public static void block() {
        MinecraftOptions options = Laby.labyAPI().minecraft().options();
        if (options == null) return;

        if (isGameplayInputActive()) {
            MinecraftInputMapping perspectiveToggle = perspectiveToggleMapping(options);
            if (perspectiveToggle != null) {
                perspectiveToggle.unpress();
            }
        }
        options.setPerspective(Perspective.FIRST_PERSON);
    }

    public static boolean consumePerspectiveTogglePresses() {
        if (!isGameplayInputActive()) return false;

        MinecraftOptions options = Laby.labyAPI().minecraft().options();
        if (options == null) return false;

        MinecraftInputMapping perspectiveToggle = perspectiveToggleMapping(options);
        if (perspectiveToggle == null) return false;

        boolean wasQueuedOrDown = perspectiveToggle.isDown();
        perspectiveToggle.unpress();
        return wasQueuedOrDown;
    }

    public static boolean isPerspectiveToggleKey(net.labymod.api.client.gui.screen.key.Key key) {
        MinecraftOptions options = Laby.labyAPI().minecraft().options();
        if (key == null || options == null) return false;

        MinecraftInputMapping perspectiveToggle = perspectiveToggleMapping(options);
        return perspectiveToggle != null
            && (key.equals(perspectiveToggle.key()) || key.getId() == perspectiveToggle.getKeyCode());
    }

    public static boolean isGameplayInputActive() {
        return Laby.labyAPI().minecraft().isMouseLocked();
    }

    private static MinecraftInputMapping perspectiveToggleMapping(MinecraftOptions options) {
        return options.getInputMapping(TOGGLE_PERSPECTIVE_MAPPING);
    }
}
