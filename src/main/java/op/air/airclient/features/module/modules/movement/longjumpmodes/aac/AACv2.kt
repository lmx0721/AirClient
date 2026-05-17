/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.longjumpmodes.aac

import op.air.airclient.features.module.modules.movement.longjumpmodes.LongJumpMode
import op.air.airclient.utils.movement.MovementUtils

object AACv2 : LongJumpMode("AACv2") {
    override fun onUpdate() {
        mc.thePlayer.jumpMovementFactor = 0.09f
        mc.thePlayer.motionY += 0.01320999999999999
        mc.thePlayer.jumpMovementFactor = 0.08f
        MovementUtils.strafe()
    }
}