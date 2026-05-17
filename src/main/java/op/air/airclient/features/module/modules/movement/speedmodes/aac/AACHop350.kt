/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.speedmodes.aac

import op.air.airclient.event.EventState
import op.air.airclient.event.MotionEvent
import op.air.airclient.features.module.modules.movement.speedmodes.SpeedMode
import op.air.airclient.utils.extensions.isInLiquid
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.extensions.tryJump

object AACHop350 : SpeedMode("AACHop3.5.0") {

    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (event.eventState == EventState.POST && thePlayer.isMoving && !thePlayer.isInLiquid && !mc.thePlayer.isSneaking) {
            thePlayer.jumpMovementFactor += 0.00208f
            if (thePlayer.fallDistance <= 1f) {
                if (thePlayer.onGround) {
                    thePlayer.tryJump()
                    thePlayer.motionX *= 1.0118f
                    thePlayer.motionZ *= 1.0118f
                } else {
                    thePlayer.motionY -= 0.0147f
                    thePlayer.motionX *= 1.00138f
                    thePlayer.motionZ *= 1.00138f
                }
            }
        }
    }

    override fun onEnable() {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.onGround) {
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0
        }
    }

    override fun onDisable() {
        mc.thePlayer?.jumpMovementFactor = 0.02f
    }
}