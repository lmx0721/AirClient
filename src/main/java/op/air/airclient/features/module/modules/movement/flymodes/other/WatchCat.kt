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

import op.air.airclient.features.module.modules.movement.Fly.startY
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode
import op.air.airclient.utils.extensions.stopXZ
import op.air.airclient.utils.kotlin.RandomUtils.nextDouble
import op.air.airclient.utils.movement.MovementUtils.strafe

object WatchCat : FlyMode("WatchCat") {
    override fun onUpdate() {
        strafe(0.15f)
        mc.thePlayer.isSprinting = true

        if (mc.thePlayer.posY < startY + 2) {
            mc.thePlayer.motionY = nextDouble(endInclusive = 0.5)
            return
        }

        if (startY > mc.thePlayer.posY) mc.thePlayer.stopXZ()
    }
}
