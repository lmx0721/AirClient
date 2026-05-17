/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.speedmodes.intave

import op.air.airclient.features.module.modules.movement.Speed
import op.air.airclient.features.module.modules.movement.speedmodes.SpeedMode
import op.air.airclient.utils.extensions.isInLiquid
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.movement.MovementUtils.strafe

/*
* Working on Intave: 14
* Tested on: mc.mineblaze.net
* Credit: @thatonecoder & @larryngton / Intave14
*/
object IntaveHop14 : SpeedMode("IntaveHop14") {

    private const val BOOST_CONSTANT = 0.003

    override fun onUpdate() {
        val player = mc.thePlayer ?: return

        if (!player.isMoving || player.isInLiquid || player.isInWeb || player.isOnLadder) return

        if (player.onGround) {
            player.motionY = 0.42 - if (Speed.intaveLowHop) 1.7E-14 else 0.0

            if (player.isSprinting) strafe(strength = Speed.strafeStrength.toDouble())

            mc.timer.timerSpeed = Speed.groundTimer
        } else {
            mc.timer.timerSpeed = Speed.airTimer
        }

        if (Speed.boost && player.motionY > 0.003 && player.isSprinting) {
            player.motionX *= 1f + (BOOST_CONSTANT * Speed.initialBoostMultiplier)
            player.motionZ *= 1f + (BOOST_CONSTANT * Speed.initialBoostMultiplier)
        }
    }
}
