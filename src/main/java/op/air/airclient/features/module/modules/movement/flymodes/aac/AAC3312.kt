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
import op.air.airclient.features.module.modules.movement.Fly.aacMotion
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode
import op.air.airclient.utils.render.RenderUtils
import org.lwjgl.input.Keyboard
import java.awt.Color

object AAC3312 : FlyMode("AAC3.3.12") {
    override fun onUpdate() {
        if (mc.thePlayer.posY < -70)
            mc.thePlayer.motionY = aacMotion.toDouble()

        mc.timer.timerSpeed = 1f

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            mc.timer.timerSpeed = 0.2f
            mc.rightClickDelayTimer = 0
        }
    }

    override fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawPlatform(-70.0, Color(0, 0, 255, 90), 1.0)
    }
}
