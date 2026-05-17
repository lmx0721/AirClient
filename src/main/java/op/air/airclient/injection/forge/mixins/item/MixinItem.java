/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.injection.forge.mixins.item;

import op.air.airclient.utils.rotation.Rotation;
import op.air.airclient.utils.rotation.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public class MixinItem {

    /**
     * Rotation modification injections. Replaces actual rotation with the current rotation to synchronize placements client-side.
     */
    @Redirect(method = "getMovingObjectPositionFromPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;rotationYaw:F"))
    private float hookCurrentRotationYaw(EntityPlayer instance) {
        Rotation rotation = RotationUtils.INSTANCE.getCurrentRotation();

        if (instance.getGameProfile() != Minecraft.getMinecraft().thePlayer.getGameProfile() || rotation == null) {
            return instance.rotationYaw;
        }

        return rotation.getYaw();
    }

    @Redirect(method = "getMovingObjectPositionFromPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;rotationPitch:F"))
    private float hookCurrentRotationPitch(EntityPlayer instance) {
        Rotation rotation = RotationUtils.INSTANCE.getCurrentRotation();

        if (instance.getGameProfile() != Minecraft.getMinecraft().thePlayer.getGameProfile() || rotation == null) {
            return instance.rotationPitch;
        }

        return rotation.getPitch();
    }
}
