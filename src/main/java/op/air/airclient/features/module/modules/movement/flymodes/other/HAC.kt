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

import op.air.airclient.features.module.modules.movement.flymodes.FlyMode

object HAC : FlyMode("HAC") {
    override fun onUpdate() {
        mc.thePlayer.motionX *= 0.8
        mc.thePlayer.motionZ *= 0.8
        mc.thePlayer.motionY = if (mc.thePlayer.motionY <= -0.42) 0.42 else -0.42
    }
}
