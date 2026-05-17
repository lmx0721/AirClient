/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.speedmodes.vulcan

import op.air.airclient.features.module.modules.movement.speedmodes.SpeedMode
import op.air.airclient.utils.extensions.isInLiquid
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.extensions.tryJump
import op.air.airclient.utils.movement.MovementUtils.strafe

object VulcanHop : SpeedMode("VulcanHop") {
    override fun onUpdate() {
        val player = mc.thePlayer ?: return
        if (player.isInLiquid || player.isInWeb || player.isOnLadder) return

        if (player.isMoving) {
            if (player.isAirBorne && player.fallDistance > 2) {
                mc.timer.timerSpeed = 1f
                return
            }

            if (player.onGround) {
                player.tryJump()
                if (player.motionY > 0) {
                    mc.timer.timerSpeed = 1.1453f
                }
                strafe(0.4815f)
            } else {
                if (player.motionY < 0) {
                    mc.timer.timerSpeed = 0.9185f
                }
            }
        } else {
            mc.timer.timerSpeed = 1f
        }
    }
}