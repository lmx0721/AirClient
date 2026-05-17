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
import op.air.airclient.utils.client.PacketUtils.sendPacket
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

object AAC316 : FlyMode("AAC3.1.6-Gomme") {
    private var tick = 0
    private var noFlag = false

    override fun onUpdate() {
        mc.thePlayer.capabilities.isFlying = true

        if (tick == 2) {
            mc.thePlayer.motionY += 0.05
        } else if (tick > 2) {
            mc.thePlayer.motionY -= 0.05
            tick = 0
        }

        tick++

        if (!noFlag)
            sendPacket(
                C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround)
            )

        if (mc.thePlayer.posY <= 0.0) noFlag = true
    }

    override fun onDisable() {
        noFlag = false
    }
}
