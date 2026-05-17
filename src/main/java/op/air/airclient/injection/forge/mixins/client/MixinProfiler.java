/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.injection.forge.mixins.client;

import op.air.airclient.event.EventManager;
import op.air.airclient.event.Render2DEvent;
import op.air.airclient.utils.client.ClassUtils;
import op.air.airclient.utils.client.ClientUtils;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Profiler.class)
public class MixinProfiler {

    @Inject(method = "startSection", at = @At("HEAD"))
    private void startSection(String name, CallbackInfo callbackInfo) {
        ClientUtils.INSTANCE.setProfilerName(name);

        if (name.equals("bossHealth") && ClassUtils.INSTANCE.hasClass("net.labymod.api.LabyModAPI")) {
            EventManager.INSTANCE.call(new Render2DEvent(0F));
        }
    }

    @Inject(method = "endSection", at = @At("HEAD"))
    private void endSection(CallbackInfo ci) {
        ClientUtils.INSTANCE.setProfilerName("");
    }

}