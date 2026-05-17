/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.longjumpmodes.ncp

import op.air.airclient.event.MoveEvent
import op.air.airclient.features.module.modules.movement.LongJump.canBoost
import op.air.airclient.features.module.modules.movement.LongJump.jumped
import op.air.airclient.features.module.modules.movement.LongJump.ncpBoost
import op.air.airclient.features.module.modules.movement.longjumpmodes.LongJumpMode
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.movement.MovementUtils.speed

object NCP : LongJumpMode("NCP") {
    override fun onUpdate() {
        speed *= if (canBoost) ncpBoost else 1f
        canBoost = false
    }

    override fun onMove(event: MoveEvent) {
        if (!mc.thePlayer.isMoving && jumped) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            event.zeroXZ()
        }
    }
}