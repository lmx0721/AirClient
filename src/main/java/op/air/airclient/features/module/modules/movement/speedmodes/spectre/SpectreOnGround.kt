/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.speedmodes.spectre

import op.air.airclient.event.MoveEvent
import op.air.airclient.features.module.modules.movement.speedmodes.SpeedMode
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.extensions.toRadians
import kotlin.math.cos
import kotlin.math.sin

object SpectreOnGround : SpeedMode("SpectreOnGround") {
    private var speedUp = 0
    override fun onMove(event: MoveEvent) {
        if (!mc.thePlayer.isMoving || mc.thePlayer.movementInput.jump) return
        if (speedUp >= 10) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                speedUp = 0
            }
            return
        }
        if (mc.thePlayer.onGround && mc.gameSettings.keyBindForward.isKeyDown) {
            val f = mc.thePlayer.rotationYaw.toRadians()
            mc.thePlayer.motionX -= sin(f) * 0.145f
            mc.thePlayer.motionZ += cos(f) * 0.145f
            event.x = mc.thePlayer.motionX
            event.y = 0.005
            event.z = mc.thePlayer.motionZ
            speedUp++
        }
    }
}