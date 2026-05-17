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

import op.air.airclient.features.module.modules.movement.Fly.neruxVaceTicks
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode

object NeruxVace : FlyMode("NeruxVace") {
    private var tick = 0
    override fun onUpdate() {
        if (!mc.thePlayer.onGround)
            tick++

        if (tick >= neruxVaceTicks && !mc.thePlayer.onGround) {
            tick = 0
            mc.thePlayer.motionY = .015
        }
    }
}
