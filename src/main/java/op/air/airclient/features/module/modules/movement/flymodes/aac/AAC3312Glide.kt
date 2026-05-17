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

import op.air.airclient.features.module.modules.movement.flymodes.FlyMode

object AAC3312Glide : FlyMode("AAC3.3.12-Glide") {
    private var tick = 0

    override fun onUpdate() {
        if (!mc.thePlayer.onGround)
            tick++

        if (tick == 2) mc.timer.timerSpeed = 1f
        else if (tick == 12) mc.timer.timerSpeed = 0.1f
        else if (tick >= 12 && !mc.thePlayer.onGround) {
            tick = 0
            mc.thePlayer.motionY = .015
        }
    }
}
