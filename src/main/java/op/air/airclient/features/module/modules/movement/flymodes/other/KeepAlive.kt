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

import op.air.airclient.features.module.modules.movement.Fly.vanillaSpeed
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode
import op.air.airclient.utils.client.PacketUtils.sendPacket
import op.air.airclient.utils.movement.MovementUtils.strafe
import net.minecraft.network.play.client.C00PacketKeepAlive

object KeepAlive : FlyMode("KeepAlive") {
    override fun onUpdate() {
        sendPacket(C00PacketKeepAlive())
        mc.thePlayer.capabilities.isFlying = false

        mc.thePlayer.motionY = when {
            mc.gameSettings.keyBindJump.isKeyDown -> vanillaSpeed.toDouble()
            mc.gameSettings.keyBindSneak.isKeyDown -> -vanillaSpeed.toDouble()
            else -> 0.0
        }

        strafe(vanillaSpeed, true)
    }
}
