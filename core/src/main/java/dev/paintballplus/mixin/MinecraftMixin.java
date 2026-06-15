package dev.paintballplus.mixin;

import dev.paintballplus.PerspectiveBlocker;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.Minecraft")
public class MinecraftMixin {

    @Inject(
        method = "runTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/settings/GameSettings;keyBindTogglePerspective:Lnet/minecraft/client/settings/KeyBinding;",
            opcode = Opcodes.GETFIELD,
            shift = At.Shift.BEFORE
        ),
        require = 0,
        remap = false
    )
    private void paintballplus$blockPerspectiveToggleNamed(CallbackInfo callbackInfo) {
        PerspectiveBlocker.blockIfEnabled();
    }

    @Inject(
        method = "func_71407_l",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/settings/GameSettings;field_151457_aa:Lnet/minecraft/client/settings/KeyBinding;",
            opcode = Opcodes.GETFIELD,
            shift = At.Shift.BEFORE
        ),
        require = 0,
        remap = false
    )
    private void paintballplus$blockPerspectiveToggleSrg(CallbackInfo callbackInfo) {
        PerspectiveBlocker.blockIfEnabled();
    }
}
