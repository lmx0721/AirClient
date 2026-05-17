/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.flymodes.other

import op.air.airclient.features.module.modules.movement.flymodes.FlyMode
import net.minecraft.util.EnumParticleTypes

object Jetpack : FlyMode("Jetpack") {
    override fun onUpdate() {
        if (!mc.gameSettings.keyBindJump.isKeyDown)
            return

        // Let's bring back the particles, this mode is useless anyway
        mc.effectRenderer.spawnEffectParticle(
            EnumParticleTypes.FLAME.particleID,
            mc.thePlayer.posX,
            mc.thePlayer.posY + 0.2,
            mc.thePlayer.posZ,
            -mc.thePlayer.motionX,
            -0.5,
            -mc.thePlayer.motionZ
        )

        mc.thePlayer.motionY += 0.15

        mc.thePlayer.motionX *= 1.1
        mc.thePlayer.motionZ *= 1.1
    }
}
