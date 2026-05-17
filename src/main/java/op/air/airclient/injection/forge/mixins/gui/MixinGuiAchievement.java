/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.injection.forge.mixins.gui;

import op.air.airclient.features.module.modules.render.AntiBlind;
import net.minecraft.client.gui.achievement.GuiAchievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiAchievement.class)
public class MixinGuiAchievement {

    @Inject(method = "displayAchievement", at = @At("HEAD"), cancellable = true)
    private void injectAchievements(CallbackInfo ci) {

        if (AntiBlind.INSTANCE.handleEvents() && AntiBlind.INSTANCE.getAchievements()) {
            // Cancel Achievement Display Packet
            ci.cancel();
        }
    }

    @Inject(method = "updateAchievementWindow", at = @At("HEAD"), cancellable = true)
    private void injectAchievementWindows(CallbackInfo ci) {

        if (AntiBlind.INSTANCE.handleEvents() && AntiBlind.INSTANCE.getAchievements()) {
            // Cancel Achievement Window Packet
            ci.cancel();
        }
    }
}
