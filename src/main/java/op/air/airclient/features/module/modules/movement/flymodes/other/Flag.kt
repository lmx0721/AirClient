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
import op.air.airclient.utils.extensions.component1
import op.air.airclient.utils.extensions.component2
import op.air.airclient.utils.extensions.component3
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

object Flag : FlyMode("Flag") {
    override fun onUpdate() {
        val (x, y, z) = mc.thePlayer

        sendPackets(
            C04PacketPlayerPosition(
                x + mc.thePlayer.motionX * 999,
                y + (if (mc.gameSettings.keyBindJump.isKeyDown) 1.5624 else 0.00000001) - if (mc.gameSettings.keyBindSneak.isKeyDown) 0.0624 else 0.00000002,
                z + mc.thePlayer.motionZ * 999,
                true
            ),
            C04PacketPlayerPosition(
                x + mc.thePlayer.motionX * 999,
                y - 6969,
                z + mc.thePlayer.motionZ * 999,
                true
            )
        )

        mc.thePlayer.setPosition(x + mc.thePlayer.motionX * 11, y, z + mc.thePlayer.motionZ * 11)
        mc.thePlayer.motionY = 0.0
    }
}
