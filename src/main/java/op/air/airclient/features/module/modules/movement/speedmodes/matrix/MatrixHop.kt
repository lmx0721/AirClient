/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.speedmodes.matrix

import op.air.airclient.features.module.modules.movement.Speed
import op.air.airclient.features.module.modules.movement.speedmodes.SpeedMode
import op.air.airclient.features.module.modules.world.scaffolds.Scaffold
import op.air.airclient.utils.extensions.isInLiquid
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.movement.MovementUtils.speed
import op.air.airclient.utils.movement.MovementUtils.strafe

/*
* Working on Matrix: 7.11.8
* Tested on: eu.loyisa.cn & anticheat.com
* Credit: @EclipsesDev
*/
object MatrixHop : SpeedMode("MatrixHop") {

    override fun onUpdate() {
        val player = mc.thePlayer ?: return
        if (player.isInLiquid || player.isInWeb || player.isOnLadder) return

        if (Speed.matrixLowHop) player.jumpMovementFactor = 0.026f

        if (player.isMoving) {
            if (player.onGround) {
                strafe(if (!Scaffold.handleEvents()) speed + Speed.extraGroundBoost else speed)
                player.motionY = 0.42 - if (Speed.matrixLowHop) 3.48E-3 else 0.0
            } else {
                if (!Scaffold.handleEvents() && speed < 0.19) {
                    strafe()
                }
            }

            if (player.fallDistance <= 0.4 && player.moveStrafing == 0f) {
                player.speedInAir = 0.02035f
            } else {
                player.speedInAir = 0.02f
            }
        }
    }
}
