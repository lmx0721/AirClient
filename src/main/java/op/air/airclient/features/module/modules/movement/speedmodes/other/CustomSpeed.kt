/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.speedmodes.other

import op.air.airclient.features.module.modules.movement.Speed
import op.air.airclient.features.module.modules.movement.Speed.customAirStrafe
import op.air.airclient.features.module.modules.movement.Speed.customAirTimer
import op.air.airclient.features.module.modules.movement.Speed.customAirTimerTick
import op.air.airclient.features.module.modules.movement.Speed.customGroundStrafe
import op.air.airclient.features.module.modules.movement.Speed.customGroundTimer
import op.air.airclient.features.module.modules.movement.Speed.customY
import op.air.airclient.features.module.modules.movement.Speed.notOnConsuming
import op.air.airclient.features.module.modules.movement.Speed.notOnFalling
import op.air.airclient.features.module.modules.movement.Speed.notOnVoid
import op.air.airclient.features.module.modules.movement.speedmodes.SpeedMode
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.extensions.stopXZ
import op.air.airclient.utils.extensions.stopY
import op.air.airclient.utils.extensions.tryJump
import op.air.airclient.utils.movement.FallingPlayer
import op.air.airclient.utils.movement.MovementUtils.strafe
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion

object CustomSpeed : SpeedMode("Custom") {

    override fun onMotion() {
        val player = mc.thePlayer ?: return
        val heldItem = player.heldItem

        val fallingPlayer = FallingPlayer()
        if (notOnVoid && fallingPlayer.findCollision(500) == null
            || notOnFalling && player.fallDistance > 2.5f
            || notOnConsuming && player.isUsingItem
            && (heldItem.item is ItemFood
                    || heldItem.item is ItemPotion
                    || heldItem.item is ItemBucketMilk)
        ) {

            if (player.onGround) player.tryJump()
            mc.timer.timerSpeed = 1f
            return
        }

        if (player.isMoving) {
            if (player.onGround) {
                if (customGroundStrafe > 0) {
                    strafe(customGroundStrafe)
                }

                mc.timer.timerSpeed = customGroundTimer
                player.motionY = customY.toDouble()
            } else {
                if (customAirStrafe > 0) {
                    strafe(customAirStrafe)
                }

                if (player.ticksExisted % customAirTimerTick == 0) {
                    mc.timer.timerSpeed = customAirTimer
                } else {
                    mc.timer.timerSpeed = 1f
                }
            }
        }
    }

    override fun onEnable() {
        val player = mc.thePlayer ?: return

        if (Speed.resetXZ) player.stopXZ()
        if (Speed.resetY) player.stopY()

        super.onEnable()
    }

}