/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.speedmodes.ncp

import op.air.airclient.features.module.modules.movement.speedmodes.SpeedMode
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.extensions.tryJump
import op.air.airclient.utils.movement.MovementUtils.strafe
import op.air.airclient.utils.timing.TickTimer

object Frame : SpeedMode("Frame") {
    private var motionTicks = 0
    private var move = false
    private val tickTimer = TickTimer()
    override fun onMotion() {
        if (mc.thePlayer.isMoving) {
            val speed = 4.25
            if (mc.thePlayer.onGround) {
                mc.thePlayer.tryJump()
                if (motionTicks == 1) {
                    tickTimer.reset()
                    if (move) {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                        move = false
                    }
                    motionTicks = 0
                } else motionTicks = 1
            } else if (!move && motionTicks == 1 && tickTimer.hasTimePassed(5)) {
                mc.thePlayer.motionX *= speed
                mc.thePlayer.motionZ *= speed
                move = true
            }
            if (!mc.thePlayer.onGround) strafe()
            tickTimer.update()
        }
    }

}