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

object AACv1 : LongJumpMode("AACv1") {
    override fun onUpdate() {
        mc.thePlayer.motionY += 0.05999
        MovementUtils.speed *= 1.08f
    }
}