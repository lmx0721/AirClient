/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.speedmodes.hypixel

import op.air.airclient.features.module.modules.movement.speedmodes.SpeedMode
import op.air.airclient.utils.extensions.isInLiquid
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.extensions.tryJump
import op.air.airclient.utils.movement.MovementUtils.strafe

object HypixelHop : SpeedMode("HypixelHop") {
    override fun onStrafe() {
        val player = mc.thePlayer ?: return
        if (player.isInLiquid)
            return

        if (player.onGround && player.isMoving) {
            if (player.isUsingItem) {
                player.tryJump()
            } else {
                player.tryJump()
                strafe(0.4f)
            }
        }

    }
}
