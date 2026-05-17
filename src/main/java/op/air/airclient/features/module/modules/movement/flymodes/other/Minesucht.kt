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
import op.air.airclient.utils.client.PacketUtils.sendPackets
import op.air.airclient.utils.extensions.*
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

object Minesucht : FlyMode("Minesucht") {
    private var minesuchtTP = 0L

    override fun onUpdate() {
        val (x, y, z) = mc.thePlayer

        if (!mc.gameSettings.keyBindForward.isKeyDown) return

        if (System.currentTimeMillis() - minesuchtTP > 99) {
            val vec = mc.thePlayer.eyes + mc.thePlayer.getLook(1f) * 7.0

            if (mc.thePlayer.fallDistance > 0.8) {
                sendPackets(
                    C04PacketPlayerPosition(x, y + 50, z, false),
                    C04PacketPlayerPosition(x, y + 20, z, true)
                )
                mc.thePlayer.fall(100f, 100f)
                mc.thePlayer.fallDistance = 0f
            }
            sendPackets(
                C04PacketPlayerPosition(vec.xCoord, y + 50, vec.zCoord, true),
                C04PacketPlayerPosition(x, y, z, false),
                C04PacketPlayerPosition(vec.xCoord, y, vec.zCoord, true),
                C04PacketPlayerPosition(x, y, z, false)
            )
            minesuchtTP = System.currentTimeMillis()
        } else {
            sendPackets(
                C04PacketPlayerPosition(x, y, z, false),
                C04PacketPlayerPosition(x, y, z, true)
            )
        }
    }
}
