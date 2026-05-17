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

import op.air.airclient.features.module.modules.movement.Fly.vanillaSpeed
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode
import op.air.airclient.utils.client.PacketUtils.sendPackets
import op.air.airclient.utils.extensions.component1
import op.air.airclient.utils.extensions.component2
import op.air.airclient.utils.extensions.component3
import op.air.airclient.utils.extensions.toRadiansD
import op.air.airclient.utils.movement.MovementUtils.strafe
import op.air.airclient.utils.timing.MSTimer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import kotlin.math.cos
import kotlin.math.sin

object MineSecure : FlyMode("MineSecure") {
    private val timer = MSTimer()

    override fun onUpdate() {
        mc.thePlayer.capabilities.isFlying = false

        mc.thePlayer.motionY =
            if (mc.gameSettings.keyBindSneak.isKeyDown) 0.0
            else -0.01

        strafe(vanillaSpeed, true)

        if (!timer.hasTimePassed(150) || !mc.gameSettings.keyBindJump.isKeyDown)
            return

        val (x, y, z) = mc.thePlayer

        sendPackets(
            C04PacketPlayerPosition(x, y + 5, z, false),
            C04PacketPlayerPosition(0.5, -1000.0, 0.5, false)
        )

        val yaw = mc.thePlayer.rotationYaw.toRadiansD()

        mc.thePlayer.setPosition(x - sin(yaw) * 0.4, y, z + cos(yaw) * 0.4)
        timer.reset()
    }
}
