/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.longjumpmodes.other

import op.air.airclient.features.module.modules.movement.longjumpmodes.LongJumpMode

object Hycraft : LongJumpMode("Hycraft") {
    override fun onUpdate() {
        if (mc.thePlayer.motionY < 0) {
            mc.thePlayer.motionY *= 0.75f
            mc.thePlayer.jumpMovementFactor = 0.055f
        } else {
            mc.thePlayer.motionY += 0.02f
            mc.thePlayer.jumpMovementFactor = 0.08f
        }
    }
}