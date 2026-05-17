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

import op.air.airclient.event.MoveEvent
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode
import op.air.airclient.utils.extensions.toRadiansD
import op.air.airclient.utils.timing.TickTimer
import kotlin.math.cos
import kotlin.math.sin

object CubeCraft : FlyMode("CubeCraft") {
    private val tickTimer = TickTimer()

    override fun onUpdate() {
        mc.timer.timerSpeed = 0.6f
        tickTimer.update()
    }

    override fun onMove(event: MoveEvent) {
        val yaw = mc.thePlayer.rotationYaw.toRadiansD()

        if (tickTimer.hasTimePassed(2)) {
            event.x = -sin(yaw) * 2.4
            event.z = cos(yaw) * 2.4
            tickTimer.reset()
        } else {
            event.x = -sin(yaw) * 0.2
            event.z = cos(yaw) * 0.2
        }
    }
}
