/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.flymodes.aac

import op.air.airclient.features.module.modules.movement.Fly.aacFast
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode

object AAC305 : FlyMode("AAC3.0.5") {
    private var tick = 0

    override fun onUpdate() {
        if (tick == 2)
            mc.thePlayer.motionY = 0.1
        else if (tick > 2) tick = 0

        if (aacFast) {
            if (mc.thePlayer.movementInput.moveStrafe == 0f) mc.thePlayer.jumpMovementFactor = 0.08f
            else mc.thePlayer.jumpMovementFactor = 0f
        }

        tick++
    }
}
