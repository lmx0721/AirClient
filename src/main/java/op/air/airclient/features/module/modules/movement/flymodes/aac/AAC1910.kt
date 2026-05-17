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

import op.air.airclient.event.Render3DEvent
import op.air.airclient.features.module.modules.movement.Fly.aacSpeed
import op.air.airclient.features.module.modules.movement.Fly.startY
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode
import op.air.airclient.utils.client.PacketUtils.sendPacket
import op.air.airclient.utils.movement.MovementUtils.strafe
import op.air.airclient.utils.render.RenderUtils.drawPlatform
import net.minecraft.network.play.client.C03PacketPlayer
import java.awt.Color

object AAC1910 : FlyMode("AAC1.9.10") {

    private var jump = 0.0

    override fun onEnable() {
        jump = 3.8
    }

    override fun onUpdate() {
        if (mc.gameSettings.keyBindJump.isKeyDown)
            jump += 0.2

        if (mc.gameSettings.keyBindSneak.isKeyDown)
            jump -= 0.2

        if (startY + jump > mc.thePlayer.posY) {
            sendPacket(C03PacketPlayer(true))
            mc.thePlayer.motionY = 0.8
            strafe(aacSpeed)
        }

        // TODO: Doesn't this always overwrite the strafe(aacSpeed)?
        strafe()
    }

    override fun onRender3D(event: Render3DEvent) {
        drawPlatform(startY + jump, Color(0, 0, 255, 90), 1.0)
    }
}
